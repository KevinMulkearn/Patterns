package com.mulkearn.kevin.patterns;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class PhyllotaxisActivity extends AppCompatActivity {

    LinearLayout phyloView;
    SeekBar c_seeker, max_seeker, size_seeker, angle_seeker;
    Intent i_phylo;

    int width, height;
    Resources resources;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;

    int c = 20;
    int n = 0;
    int max = 1000;
    int size = 5;
    double angle = 137.5;
    double col = 200;
    int colorOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phyllotaxis);

        phyloView = (LinearLayout) findViewById(R.id.phyloView);
        c_seeker = (SeekBar) findViewById(R.id.c_seeker);
        max_seeker = (SeekBar) findViewById(R.id.max_seeker);
        size_seeker = (SeekBar) findViewById(R.id.size_seeker);
        angle_seeker = (SeekBar) findViewById(R.id.angle_seeker);

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

        max_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                max = progress;
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        size_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        angle_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp = progress;
                angle = mapValuesDouble((double) temp, 0, 30, 137.0, 138.0);
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
        // set variables
        double a = n * angle;
        double r = c * Math.sqrt(n);
        double x = r * Math.cos(Math.toRadians(a));
        double y = r * Math.sin(Math.toRadians(a));

        // create the Paint and set its color
        if (colorOption == 1){
            col = mapValuesInt(n, 0, max, 0, 360); // map hue from 0 to 360
        } else if (colorOption == 2){
            col = n % 360;
        } else if (colorOption == 3){
            col = a % 360; // loop back to first color
        } else if (colorOption == 4){
            col = (a - n) % 360; // follow spiral path
        }
        float[] hsv = {(int) col,100,100};
        Paint paint = new Paint();
        paint.setColor(Color.HSVToColor(hsv));

        // Draw dot
        canvas.drawCircle((float) x, (float) y, size, paint);
        n++;
        if(n < max){
            point(n);
        }

        //Display
        phyloView.setBackground(bmd);
    }

    //Maps one range of values to another
    public int mapValuesInt(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public double mapValuesDouble(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phyllotaxis_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_range:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                colorOption = 1;
                drawPhyllo();
                return true;
            case R.id.repeat_loop:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                colorOption = 2;
                drawPhyllo();
                return true;
            case R.id.spectrum:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                colorOption = 3;
                drawPhyllo();
                return true;
            case R.id.path_follow:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                colorOption = 4;
                drawPhyllo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
