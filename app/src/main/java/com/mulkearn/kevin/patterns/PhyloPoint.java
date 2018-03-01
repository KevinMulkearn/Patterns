package com.mulkearn.kevin.patterns;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PhyloPoint extends Canvas{

    private float x;
    private float y;
    private int r = 10;
    private Paint paint = new Paint(Color.CYAN);

    public PhyloPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void show(){
        drawCircle(x, y, r, paint);
    }


}
