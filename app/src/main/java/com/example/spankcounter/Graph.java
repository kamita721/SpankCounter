package com.example.spankcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Graph extends View {

    Paint paintGraph;
    Paint paintDebug;
    Paint paintThresholdMax;
    Paint paintThresholdMin;

    int thresholdMax=1;
    int thresholdMin=0;

    int height;
    int width;
    int[] samples;
    int sampleStartI;
    int maxSampleValue;

    boolean isHigh;

    Runnable onRisingCallback;
    Runnable onFallingCallback;

    public Graph(Context context) {
        super(context);
        init(null, 0);
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Graph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        paintGraph = new Paint();
        paintGraph.setARGB(255,255,255,255);

        paintDebug = new Paint();
        paintDebug.setARGB(255,0,0,0);

        paintThresholdMax = new Paint();
        paintThresholdMax.setARGB(255,255,0,0);

        paintThresholdMin = new Paint();
        paintThresholdMin.setARGB(255,0,255,0);

        height = getHeight();
        width = getWidth();
        samples = new int[width];
        sampleStartI=0;

        thresholdMax = height;
    }

    protected void onSizeChanged (int w,
                                  int h,
                                  int oldw,
                                  int oldh){
        height=h;
        width=w;
        samples = new int[width];

        thresholdMax = height * 22 / 25;
        thresholdMin = height * 1 / 4;
    }

    public void setMaxSampleValue(int maxSampleValue){
        this.maxSampleValue = maxSampleValue;
    }

    public void addSample(int sample){
        int scaled = sample * height / maxSampleValue;
        sampleStartI-=1;
        if(sampleStartI<0){
            sampleStartI+=samples.length;
        }
        samples[sampleStartI]=scaled;

        if(height - scaled > thresholdMax && !isHigh){
            isHigh=true;
            if(onFallingCallback != null){
                this.onFallingCallback.run();
            }
        }
        if(height - scaled< thresholdMin && isHigh){
            isHigh=false;
            if(this.onRisingCallback != null){
                this.onRisingCallback.run();
            }
        }
    }

    public void setOnRisingCallback(Runnable onRisingCallback) {
        this.onRisingCallback = onRisingCallback;
    }

    public void setOnFallingCallback(Runnable onFallingCallback) {
        this.onFallingCallback = onFallingCallback;
    }

    float touchX=0;
    float touchY=0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int x=0; x<width; x++){
            int y=height-samples[(sampleStartI + x) % width];

            Paint p;
            if(y< thresholdMin){
                p=paintThresholdMin;
            }else if(y> thresholdMax){
                p=paintThresholdMax;
            }else{
                p=paintGraph;
            }

            canvas.drawLine(x,height,x,y, p);
        }

        canvas.drawLine(0, thresholdMax,width, thresholdMax,paintThresholdMax);
        canvas.drawLine(0, thresholdMin,width, thresholdMin,paintThresholdMin);

//        canvas.drawCircle(touchX, touchY, 10, paintDebug);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX=event.getX();
        touchY=event.getY();

        float distToMin=Math.abs(touchY - thresholdMin);
        float distToMax=Math.abs(touchY - thresholdMax);

        if(distToMin<distToMax){
            thresholdMin =(int)touchY;
        }else{
            thresholdMax =(int)touchY;
        }

        if(thresholdMax <= thresholdMin){
            thresholdMax = thresholdMin +1;
        }

        this.invalidate();
        return true;
    }
}
