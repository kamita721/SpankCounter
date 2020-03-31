package com.example.spankcounter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class SoundMonitor {
    AudioRecord microphone;
    int bufferSize;
    int maxSampleValue = Short.MAX_VALUE;

    int sampleRate = 44100; //only value guarenteed to work on all devices
    int bucketSizeMS = 3;
    int bucketSize = sampleRate * bucketSizeMS / 1000;

    public SoundMonitor(){
        getMicrophone();
    }

    private AudioRecord getMicrophone(){
        if(microphone != null){
            return microphone;
        }
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        bufferSize=AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat)*2;
        try {
            microphone = new AudioRecord(AudioSource.DEFAULT,  sampleRate, channelConfig, audioFormat, bufferSize);
            microphone.startRecording();
        } catch (Exception e){
            Log.e("SoundMonitor", "Error initializing microphone", e);
            microphone.release();
            microphone = null;
        }
        return microphone;
    }

    public void dumpSamples(Graph graph) {
        getMicrophone();
        if(microphone == null){
            return;
        }
        short[] data = new short[bufferSize];
        int read = microphone.read(data, 0, bufferSize / 10, AudioRecord.READ_BLOCKING);
        int currMax=0;
        int prevMax=0;
        int bucketMax=0;
        for(int i=0; i<read; i++){
            if (data[i] > bucketMax) {
                bucketMax=data[i];
            }
            if(i%bucketSize==0){
                graph.addSample(bucketMax);
                bucketMax=0;
            }
        }

    }
}
