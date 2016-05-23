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
import android.provider.MediaStore;

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
    private LinkedBlockingDeque<Short> leftBlocks = new LinkedBlockingDeque<Short>();
    private LinkedBlockingDeque<Short> rightBlocks = new LinkedBlockingDeque<Short>();
    private short[] buffer;

    class AudioReader extends Thread {
        AudioRecorder recorder;

        public AudioReader(AudioRecorder recorder) {
            this.recorder = recorder;
        }

        @Override
        public void run() {

            while (recorder.stopped == false) {
                try {
                    Thread.sleep(10l);
                } catch (InterruptedException e) {
                }

                if (recorder.recorder == null) {
                    return;
                }

                try {
                    int number = recorder.recorder.read(buffer, 0, buffer.length);
                    if(number<=0 || number > recorder.bufferSize){
                        continue;
                    }

                    //single channel
                    if(recorder.channel == AudioFormat.CHANNEL_IN_MONO){
                        for(int i=0;i<number;i++) {
                            if (recorder.leftBlocks.size() > recorder.sampleRate * 4) {
                                recorder.leftBlocks.poll();
                            }
                            recorder.leftBlocks.add(buffer[i]);
                        }
                    }

                    //double channels
                    else if(recorder.channel == AudioFormat.CHANNEL_IN_STEREO){
                        for(int i=0;i<number;i+=2){
                            if(recorder.leftBlocks.size() > recorder.sampleRate*4){
                                recorder.leftBlocks.poll();
                            }
                            recorder.leftBlocks.add(buffer[i]);

                            if(recorder.rightBlocks.size() > recorder.sampleRate*4){
                                recorder.rightBlocks.poll();
                            }
                            recorder.rightBlocks.add(buffer[i+1]);

                            // System.out.println(buffer[i]+":"+buffer[i+1]);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();

        if (action.equals("create")) {
            create(args.getString(0), callbackContext);
            return true;
        }

        if (action.equals("start")) {
            start(callbackContext);
            return true;
        }

        if (action.equals("stop")) {
            stop(callbackContext);
            return true;
        }

        if (action.equals("read")) {
            read(args.getInt(0),args.getInt(1), callbackContext);
            return true;
        }

        if (action.equals("clear")) {
            clear(callbackContext);
            return true;
        }

        return false;
    }


    private void create(String configStr, final CallbackContext callbackContext) {

        try {
            JSONObject config = new JSONObject(configStr);
            this.source = config.getInt("source");
            this.sampleRate = config.getInt("sampleRate");
            this.channel = config.getInt("channel");
            this.format = config.getInt("format");

            System.out.println(this.channel+":"+AudioFormat.CHANNEL_IN_MONO+":"+AudioFormat.CHANNEL_IN_STEREO);

            if (channel == AudioFormat.CHANNEL_IN_MONO) {
                this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            } else if(channel == AudioFormat.CHANNEL_IN_STEREO){
                this.bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            }

            System.out.println("bufferSize:"+this.bufferSize);

            this.recorder = new AudioRecord(source, sampleRate, channel, format, bufferSize);
            this.buffer = new short[bufferSize];

            System.out.println("Channel count:"+this.recorder.getChannelCount());


            callbackContext.success(0);

        } catch (Exception ex) {
            ex.printStackTrace();
            callbackContext.error(ex.getMessage());
        }

    }

    private void start(final CallbackContext callbackContext) {
        try {
            this.recorder.startRecording();
            this.stopped = false;
            new AudioReader(this).start();
            callbackContext.success(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            callbackContext.error(ex.getMessage());
        }
    }

    private void stop(final CallbackContext callbackContext) {
        try {
            if (this.recorder == null) {
                this.stopped = true;
                callbackContext.success(0);
            } else {
                this.recorder.stop();
                this.recorder.release();
                this.recorder = null;
                this.leftBlocks.clear();
                this.rightBlocks.clear();
                this.stopped = true;
                callbackContext.success(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            callbackContext.error(ex.getMessage());
        }
    }

    private void clear(final CallbackContext callbackContext) {
        this.leftBlocks.clear();
        this.rightBlocks.clear();
        callbackContext.success();
    }


    /**
     *
     * @param length the data length that upper layer wants to read
     * @param channel which channel is preferred. If channel equals 1, the data of left channel is delivered.
     *                If channel equals 2, the data of right channel is delivered. If channel equals 0, both are delivered.
     * @param callbackContext
     */
    private void read(final int length, final int channel, final CallbackContext callbackContext) {

        new Thread(new Runnable() {

            public void run() {
                try {
                    JSONObject result = new JSONObject();
                    JSONArray leftBlock = new JSONArray();
                    JSONArray rightBlock = new JSONArray();
                    result.put("leftChannel",leftBlock);
                    result.put("rightChannel", rightBlock);

                    if(channel == 1 || channel == 0) {
                        for (int i = 0; i < length; i++) {
                            leftBlock.put(i, AudioRecorder.this.leftBlocks.take());
                        }
                    }

                    if((channel == 2|| channel == 0)&&(AudioRecorder.this.channel == AudioFormat.CHANNEL_IN_STEREO)){
                        for(int i=0;i<length;i++){
                            rightBlock.put(i, AudioRecorder.this.rightBlocks.take());
                        }
                    }
                    System.out.println("left......:"+"right.......");
                    System.out.println(leftBlock.length()+":"+rightBlock.length());

                    callbackContext.success(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();

    }
}
