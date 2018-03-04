package com.mulkearn.kevin.patterns;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FractalTreeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SeekBar angleRightSeeker, angleLeftSeeker, lengthSeeker, levelsSeeker, backColorSeeker, treeColorSeeker;
    TextView angleRightView, angleLeftView, lengthView, levelsView;
    LinearLayout treeView;
    Resources resources;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;

    int width, height;
    float angleRight = 30f, angleLeft = -30f, branchLen = 400, decayLength = 0.67f, levels = 80f;
    int backHue = -1, treeHue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractal_tree);

        treeView = (LinearLayout) findViewById(R.id.treeView);
        backColorSeeker = (SeekBar) findViewById(R.id.backColorSeeker);
        treeColorSeeker = (SeekBar) findViewById(R.id.treeColorSeeker);
        angleRightSeeker = (SeekBar) findViewById(R.id.angleRightSeeker);
        angleLeftSeeker = (SeekBar) findViewById(R.id.angleLeftSeeker);
        lengthSeeker = (SeekBar) findViewById(R.id.lengthSeeker);
        levelsSeeker = (SeekBar) findViewById(R.id.levelsSeeker);
        angleRightView = (TextView) findViewById(R.id.angleRightView);
        angleLeftView = (TextView) findViewById(R.id.angleLeftView);
        lengthView = (TextView) findViewById(R.id.lengthView);
        levelsView = (TextView) findViewById(R.id.levelsView);

        //Hue seeker
        GradientDrawable hueGrad = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, buildHueColorArray());
        hueGrad.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        backColorSeeker.setBackgroundDrawable(hueGrad);
        treeColorSeeker.setBackgroundDrawable(hueGrad);

        // Toolbar and drawer setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get window size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        //create Bitmap and Canvas
        bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bm);
        bmd = new BitmapDrawable(resources, bm);

        canvas.drawColor(Color.BLACK);
        canvas.translate(width/2, height-1);
        drawTree();

        //Right angle
        angleRightSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleRight = progress;
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Left angle
        angleLeftSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleLeft = -progress;
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Initial Branch Length
        lengthSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                branchLen = progress;
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Level of branches
        levelsSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                levels = mapValuesFloat(progress, 0, 100, branchLen, 10);
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Color of tree
        backColorSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backHue = progress;
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                backColorSeeker.setVisibility(View.GONE);
            }
        });

        //Color of background
        treeColorSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                treeHue = progress;
                drawTree();
                setValues();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                treeColorSeeker.setVisibility(View.GONE);
            }
        });

    }

    public void drawTree(){
        canvas.save();
        if (backHue == -1){
            canvas.drawColor(Color.WHITE);
        } else {
            float[] back_hsv = {backHue, 100, 100};
            canvas.drawColor(Color.HSVToColor(back_hsv));
        }
        branch(branchLen);
        canvas.restore();
    }

    public void branch(float branchLen){

        //Set line color
        Paint paint = new Paint();
        paint.setStrokeWidth(10f);
        if (treeHue == -1){
            paint.setColor(Color.BLACK);
        } else if(treeHue == -2){
            double col = branchLen % 800;
            float[] hsv = {(int) col,100,100};
            paint.setColor(Color.HSVToColor(hsv));
        }else {
            float[] hsv = {treeHue,100,100};
            paint.setColor(Color.HSVToColor(hsv));
        }

        //Create line
        canvas.drawLine(0, 0, 0, -branchLen, paint);
        canvas.translate(0, -branchLen);

        if (branchLen > levels){
            canvas.save();
            canvas.rotate(angleRight);
            branch(branchLen*decayLength);
            canvas.restore();
            canvas.save();
            canvas.rotate(angleLeft);
            branch(branchLen*decayLength);
            canvas.restore();
        }
        //Draw lines
        treeView.setBackground(bmd);
    }

    public void setValues(){
        angleRightView.setText("Angle Right: " + angleRight);
        angleLeftView.setText("Angle Left: " + angleLeft);
        lengthView.setText("Trunk Length: " + branchLen);
        //levelsView.setText("Levels: " + levels);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fractal_tree_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_color:
                backColorSeeker.setVisibility(View.VISIBLE);
                return true;
            case R.id.tree_color:
                treeColorSeeker.setVisibility(View.VISIBLE);
                return true;
            case R.id.spectrum:
                treeHue = -2;
                drawTree();
                return true;
            case R.id.save:
                //Add Action
                return true;
            case R.id.reset:
                Intent i_reset = new Intent(this, FractalTreeActivity.class);
                startActivity(i_reset);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fractal_tree) {
            Intent i_fractal = new Intent(this, FractalTreeActivity.class);
            startActivity(i_fractal);
        } else if (id == R.id.phyllotaxis) {
            Intent i_phyllotaxis = new Intent(this, PhyllotaxisActivity.class);
            startActivity(i_phyllotaxis);
        } else if (id == R.id.item_3) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Maps one range of values to another
    public float mapValuesFloat(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    private int[] buildHueColorArray(){
        int[] hueArr = new int[361];
        int count = 0;
        for (int i = hueArr.length - 1; i >= 0; i--, count++) {
            hueArr[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }
        return hueArr;
    }

}
