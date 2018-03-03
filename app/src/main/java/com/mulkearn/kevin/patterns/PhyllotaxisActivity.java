package com.mulkearn.kevin.patterns;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

public class PhyllotaxisActivity extends View {

    int n = 0;
    int c = 20;

    public PhyllotaxisActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        point(canvas);
    }


    public void point(Canvas canvas){

        int col = mapValues(n, 0, 1000, 0, 360);

        // create the Paint and set its color
        float[] hsv = {col,100,100};
        Paint paint = new Paint();
        paint.setColor(Color.HSVToColor(hsv));

        // set variables
        double a = n * 137.5;
        double r = c * Math.sqrt(n);
        double x = r * Math.cos(a) + (getWidth() / 2);
        double y = r * Math.sin(a) + (getHeight() / 2);

        // Draw dot
        canvas.drawCircle((float) x, (float) y, 10, paint);
        n++;

        if(n < 1000){
            point(canvas);
        }

    }

    //Maps one range of values to another
    public int mapValues(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

}
