package edu.ktu.mysecondapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ProgressIndicator extends View {
    public static final int NOTEXECUTED = 0;
    public static final int FIRST = 20;
    public static final int SECOND = 40;
    public static final int THIRD = 60;
    public static final int FOURTH = 80;
    public static final int FIFTH = 100;
    private List<Canvas> list = new ArrayList<>();
    int state = NOTEXECUTED;

    public ProgressIndicator(Context context) {
        super(context);
    }
    public ProgressIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public ProgressIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth()/5;
        int height = getHeight();
        Paint paint;
        switch (state){
            case FIRST:
                paint = new Paint();
                paint.setColor(Color.parseColor("#ccffcc"));
                createRectangle(paint,0,canvas,width,height);
                list.add(canvas);
                break;

            case SECOND:
                paint = new Paint();
                paint.setColor(Color.parseColor("#ccffcc"));
                createRectangle(paint,0,canvas,width,height);
                paint.setColor(Color.parseColor("#99ff99"));
                createRectangle(paint,width,canvas,width*2,height);
                break;

            case THIRD:
                paint = new Paint();
                paint.setColor(Color.parseColor("#ccffcc"));
                createRectangle(paint,0,canvas,width,height);
                paint.setColor(Color.parseColor("#99ff99"));
                createRectangle(paint,width,canvas,width*2,height);
                paint.setColor(Color.parseColor("#4dff4d"));
                createRectangle(paint,width*2,canvas,width*3,height);
                break;

            case FOURTH:
                paint = new Paint();
                paint.setColor(Color.parseColor("#ccffcc"));
                createRectangle(paint,0,canvas,width,height);
                paint.setColor(Color.parseColor("#99ff99"));
                createRectangle(paint,width,canvas,width*2,height);
                paint.setColor(Color.parseColor("#4dff4d"));
                createRectangle(paint,width*2,canvas,width*3,height);
                paint.setColor(Color.parseColor("#00b300"));
                createRectangle(paint,width*3,canvas,width*4,height);
                break;

            case FIFTH:
                paint = new Paint();
                paint.setColor(Color.parseColor("#ccffcc"));
                createRectangle(paint,0,canvas,width,height);
                paint.setColor(Color.parseColor("#99ff99"));
                createRectangle(paint,width,canvas,width*2,height);
                paint.setColor(Color.parseColor("#4dff4d"));
                createRectangle(paint,width*2,canvas,width*3,height);
                paint.setColor(Color.parseColor("#00b300"));
                createRectangle(paint,width*3,canvas,width*4,height);
                paint.setColor(Color.parseColor("#006600"));
                createRectangle(paint,width*4,canvas,width*5,height);
                break;
            default:
                break;

        }
    }

    public void createRectangle(Paint paint,int margin,Canvas canvas,int width, int height){
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10f);
        canvas.drawRect(margin, 0, width, height, paint);
    }

    public void setState(int state){this.state = state;}

}
