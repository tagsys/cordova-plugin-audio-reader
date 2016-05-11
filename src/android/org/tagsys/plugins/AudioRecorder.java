package org.tagsys.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.content.Context;
import java.util.concurrent.LinkedBlockingQueue;


public class AudioRecorder extends CordovaPlugin {

  private AudioRecord recorder;
  private boolean stopped;
  private int source;
  private int sampleRate;
  private int channel;
  private int format;
  private int bufferSize;
  private LinkedBlockingQueue<Short> blocks = new LinkedBlockingQueue<Short>();
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
          short[] block = new short[recorder.bufferSize];
          int number = recorder.recorder.read(block, 0, recorder.bufferSize);

          if(number>0 && number <=recorder.bufferSize){
              for(int i=0;i<number;i++){
                //ensure no overflow
                if(recorder.blocks.size()>10485760){
                  recorder.blocks.take();
                }
                recorder.blocks.put(block[i]);
              }
          }
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
        read(args.getInt(0),callbackContext);
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
      }else{
        this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 4;
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

  private void read(int length, final CallbackContext callbackContext){

      try{

        if(length<0){
          result.put("error",-1);
        }else if(length > blocks.size()){
          result.put("error",1);
        }else{
          short[] block = new short[length];
          for(int i=0;i<length;i++){
              block[i] = this.blocks.take();
          }
          result.put("error",0);
          result.put("block", block);
        }
        callbackContext.success(result);
      }catch(Exception ex){
        ex.printStackTrace();
        callbackContext.error(ex.getMessage());
      }
  }

}
