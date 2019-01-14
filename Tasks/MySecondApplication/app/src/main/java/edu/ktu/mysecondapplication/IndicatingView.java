package edu.ktu.mysecondapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class IndicatingView extends View {

    public static final int NOTEXECUTED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int LOADING = 3;
    public static final int NUMBER = 4;

    int state = NOTEXECUTED;

    public IndicatingView (Context context){super(context);}
    public IndicatingView (Context context, AttributeSet attrs){ super(context,attrs);}
    public IndicatingView (Context context, AttributeSet attrs, int defStyleAttr){super(context,attrs,defStyleAttr);}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint;
        switch (state){
            case SUCCESS:
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(20f);

                canvas.drawLine(0,0,width/2,height,paint);
                canvas.drawLine(width/2,height,width,height/2,paint);
                break;
            case LOADING:
                paint = new Paint();
                loadingIndicator(canvas, paint, width, height);
                break;

            case FAILED:
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(20f);
                canvas.drawLine(0,0,width,height,paint);
                canvas.drawLine(0,height,width,0,paint);
                break;
            default:
                 break;

        }
    }

    public  int getState(){return state;}

    public void setState(int state){this.state = state;}

    private void loadingIndicator(Canvas canvas, Paint paint, int width, int height){
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        paint.setStrokeWidth(10f);

        Point point1_draw = new Point(width/2,0);
        Point point2_draw = new Point(0,height);
        Point point3_draw = new Point(width,height);

        Path path = new Path();
        path.moveTo(point1_draw.x,point1_draw.y);
        path.lineTo(point2_draw.x,point2_draw.y);
        path.lineTo(point3_draw.x,point3_draw.y);
        path.lineTo(point1_draw.x,point1_draw.y);
        path.close();

        canvas.drawPath(path,paint);
    }

}
