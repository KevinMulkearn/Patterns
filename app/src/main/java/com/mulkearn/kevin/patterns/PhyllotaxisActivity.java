package com.mulkearn.kevin.patterns;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.View;

public class PhyllotaxisActivity extends View {

    Paint paint;
    int n = 0;
    int c = 20;

//    double a = n * 137.5;
//    double r = c * Math.sqrt(n);
//
//    double x = r * Math.cos(a) + (getWidth() / 2);
//    double y = r * Math.sin(a) + (getHeight() / 2);


    public PhyllotaxisActivity(Context context) {
        super(context);

        // create the Paint and set its color
//        paint = new Paint();
//        paint.setColor(Color.BLACK);


//        ValueAnimator animator = ValueAnimator.ofInt(0, 50); // (start. end) values
//        animator.setDuration(2000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                //radius = (int) animation.getAnimatedValue();
//                //n = (int) animation.getAnimatedValue();
//                invalidate();
//            }
//        });
//        animator.start();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        point(canvas);
    }


    public void point(Canvas canvas){

        float[] hsv = {n,100,100};

        paint = new Paint();
        paint.setColor(Color.HSVToColor(hsv));

        double a = n * 137.5;
        double r = c * Math.sqrt(n);

        double x = r * Math.cos(a) + (getWidth() / 2);
        double y = r * Math.sin(a) + (getHeight() / 2);

        canvas.drawCircle((float) x, (float) y, 10, paint);
        n++;

        if(n < 600){
            point(canvas);
        }

    }
}
