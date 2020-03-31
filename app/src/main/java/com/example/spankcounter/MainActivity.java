package com.example.spankcounter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Graph graph;
    int spankCount;
    TextView spankCountDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getPermission();

        graph = findViewById(R.id.graph);

        spankCountDisplay = findViewById(R.id.SpankCount);
        Button resetSpankCountButton = findViewById(R.id.buttonReset);

        final SoundMonitor monitor = new SoundMonitor();
        graph.setMaxSampleValue(monitor.maxSampleValue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    monitor.dumpSamples(graph);
                    redraw();
                }
            }
        }).start();

        resetSpankCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spankCount=0;
                redraw();
            }
        });

        graph.setOnRisingCallback(new Runnable() {
            @Override
            public void run() {
                spankCount+=1;
                redraw();
            }
        });

    }

    private void redraw(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                graph.invalidate();
                spankCountDisplay.setText(Integer.toString(spankCount));
            }
        });
    }

    private void getPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        System.out.println(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO));
        System.out.println(PackageManager.PERMISSION_GRANTED);
    }

}
