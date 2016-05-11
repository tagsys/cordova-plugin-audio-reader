package org.tagsys.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.CountDownTimer;
import android.os.Environment;
import android.content.Context;
import java.util.UUID;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import android.media.AudioTrack;
import java.util.concurrent.LinkedBlockingQueue;




public class AudioRecorder extends CordovaPlugin {

  private AudioRecord recorder;
  private boolean stopped;
  private int source;
  private int sampleRate;
  private int channel;
  private int format;
  private int bufferSize;
  private int frameLength;
  private LinkedBlockingQueue<short[]> blocks = new LinkedBlockingQueue<short[]>();
  private JSONObject result = new JSONObject();

  class AudioReader extends Thread{
    AudioRecorder recorder;
    public AudioReader(AudioRecorder recorder){
      this.recorder = recorder;
    }

    @Override
    public void run(){

      while(recorder.stopped==false){
        try{
            Thread.sleep(10l);
        } catch (InterruptedException e) {
        }

        try{
          short[] block = new short[recorder.frameLength];
          recorder.recorder.read(block, 0, recorder.frameLength);
          if(recorder.blocks.size() > 4096){
            recorder.blocks.take();
          }
          recorder.blocks.put(block);
          // System.out.println("push a new block:"+recorder.blocks.size());
        }catch(Exception ex){
          ex.printStackTrace();
        }
      }
    }
  }

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    Context context = cordova.getActivity().getApplicationContext();

    if(action.equals("create")){
        create(args.getString(0), callbackContext);
        return true;
    }

    if(action.equals("start")){
        start(callbackContext);
        return true;
    }

    if (action.equals("stop")) {
        stop(callbackContext);
        return true;
    }

    if(action.equals("read")){
        read(callbackContext);
        return true;
    }

    return false;
  }


  private void create(String configStr,final CallbackContext callbackContext){

    System.out.println("comeing to the create ufnction.");

    try{
      JSONObject config = new JSONObject(configStr);
      this.source = config.getInt("source");
      this.sampleRate = config.getInt("sampleRate");
      this.channel = config.getInt("channel");
      this.format = config.getInt("format");
      this.bufferSize = 0;

      if(channel==AudioFormat.CHANNEL_IN_MONO){
        this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4;
        this.frameLength = (int)(config.getDouble("frameLength"));
      }else{
        this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 4;
        this.frameLength = (int)(config.getDouble("frameLength")*2);
      }

      this.recorder = new AudioRecord(source, sampleRate, channel, format, bufferSize);

      callbackContext.success(0);

    }catch(Exception ex){
      ex.printStackTrace();
      callbackContext.error(ex.getMessage());
    }

  }

  private void start(final CallbackContext callbackContext){
    try{
      this.recorder.startRecording();
      this.stopped = false;
      new AudioReader(this).start();
      callbackContext.success(0);
    }catch(Exception ex){
      ex.printStackTrace();
      callbackContext.error(ex.getMessage());
    }
  }

  private void stop(final CallbackContext callbackContext){
    try{
      this.recorder.stop();
      this.recorder.release();
      this.blocks.clear();
      this.stopped = true;
      callbackContext.success(0);
    }catch(Exception ex){
      ex.printStackTrace();
      callbackContext.error(ex.getMessage());
    }
  }

  private void read(final CallbackContext callbackContext){

      try{
        short[] block = this.blocks.poll();
        if(block!=null){
          result.put("error",0);
          result.put("block", block);
        }else{
          result.put("error",1);
          result.put("block",null);
        }
        callbackContext.success(result);
      }catch(Exception ex){
        ex.printStackTrace();
        callbackContext.error(ex.getMessage());
      }
  }

}
