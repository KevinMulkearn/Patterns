package com.mulkearn.kevin.patterns;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class PhyllotaxisActivity extends AppCompatActivity {

    LinearLayout phyloView;
    SeekBar c_seeker;

    int width, height;
    Resources resources;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;

    int c = 20;
    int n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phyllotaxis);

        phyloView = (LinearLayout) findViewById(R.id.phyloView);
        c_seeker = (SeekBar) findViewById(R.id.c_seeker);

        //Get window size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        //create Bitmap and Canvas
        bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bm);
        bmd = new BitmapDrawable(resources, bm);

        //Draw in the centre of the screen
        canvas.translate(width/2, height/2);

        drawPhyllo();

        c_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c = progress;
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    public void drawPhyllo(){
        //Draw
        canvas.drawColor(Color.WHITE);
        point(n);
    }

    public void point(int n){

        // create the Paint and set its color
        int col = mapValues(n, 0, 1000, 0, 360);
        float[] hsv = {col,100,100};
        Paint paint = new Paint();
        paint.setColor(Color.HSVToColor(hsv));

        // set variables
        double a = n * 137.5;
        double r = c * Math.sqrt(n);
        double x = r * Math.cos(a);// + (width / 2);
        double y = r * Math.sin(a);// + (height / 2);

        // Draw dot
        canvas.drawCircle((float) x, (float) y, 10, paint);
        n++;

        if(n < 1000){
            point(n);
        }

        //Display
        phyloView.setBackground(bmd);

    }

    //Maps one range of values to another
    public int mapValues(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

}
