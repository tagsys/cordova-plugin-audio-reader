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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Arrays;
import java.util.LinkedList;


public class AudioRecorder extends CordovaPlugin {

  private AudioRecord recorder;
  private boolean stopped;
  private int source;
  private int sampleRate;
  private int channel;
  private int format;
  private int bufferSize;
  private LinkedBlockingDeque<Short> blocks = new LinkedBlockingDeque<Short>();
  private short[] buffer;

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

        if(recorder.recorder==null){
          return;
        }

        try{
            int number = recorder.recorder.read(buffer, 0, buffer.length);
            // System.out.println(number+":"+buffer.length);
            if(number>0 && number <=recorder.bufferSize){
                for(int i=0;i<number;i++){
                  //maintain 3-second data
                  if(recorder.blocks.size()>44100*3){
                    recorder.blocks.poll();
                  }
                  recorder.blocks.add(buffer[i]);
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

    if(action.equals("clear")){
      clear(callbackContext);
      return true;
    }

    return false;
  }


  private void create(String configStr,final CallbackContext callbackContext){

    try{
      JSONObject config = new JSONObject(configStr);
      this.source = config.getInt("source");
      this.sampleRate = config.getInt("sampleRate");
      this.channel = config.getInt("channel");
      this.format = config.getInt("format");

      if(channel==AudioFormat.CHANNEL_IN_MONO){
        this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4;
      }else{
        this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 4;
      }

      this.recorder = new AudioRecord(source, sampleRate, channel, format, bufferSize);
      this.buffer = new short[bufferSize];

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
      // new AudioReader(this).start();
      new AudioReader(this).start();
      callbackContext.success(0);
    }catch(Exception ex){
      ex.printStackTrace();
      callbackContext.error(ex.getMessage());
    }
  }

  private void stop(final CallbackContext callbackContext){
    try{
      if(this.recorder == null){
        this.stopped = true;
        callbackContext.success(0);
      }else{
        this.recorder.stop();
        this.recorder.release();
        this.recorder = null;
        this.blocks.clear();
        this.stopped = true;
        callbackContext.success(0);
      }
    }catch(Exception ex){
      ex.printStackTrace();
      callbackContext.error(ex.getMessage());
    }
  }

  private void clear(final CallbackContext callbackContext){
    this.blocks.clear();
    callbackContext.success();
  }

  private void read(final int length, final CallbackContext callbackContext){

    new Thread(new Runnable(){

      public void run() {
        try{
          JSONArray block = new JSONArray();
          for(int i=0;i<length;i++){
            block.put(i,AudioRecorder.this.blocks.take());
          }
          // while (rtnBlock.length() < length) {
          //   short[] block = AudioRecorder.this.blocks.take();
          //   int offset = rtnBlock.length();
          //   for (int i = 0; i < block.length; i++) {
          //       rtnBlock.put(offset + i, block[i]);
          //   }
          // }
          // System.out.println(length+":"+rtnBlock.length());
          callbackContext.success(block);
        }catch(Exception e){
          e.printStackTrace();
          callbackContext.error(e.getMessage());
        }
      }
    }).start();

  }
}
