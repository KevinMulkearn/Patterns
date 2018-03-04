package com.mulkearn.kevin.patterns;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FractalTreeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SeekBar angleRightSeeker, angleLeftSeeker, lengthSeeker, levelsSeeker;
    TextView angleRightView, angleLeftView, lengthView, levelsView;
    LinearLayout treeView;
    Resources resources;
    Paint paint;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;

    String hexTreeColor, hexBackColor;
    int treeColor, backColor;
    int width, height;
    float angleRight = 30f, angleLeft = -30f, branchLen = 400, decayLength = 0.67f, levels = 80f;
    float scale = 1f;
    float[] tree_hsv = {0, 0, 0}, back_hsv = {0, 0, 100};
    double col = 200;
    int colorOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractal_tree);

        treeView = (LinearLayout) findViewById(R.id.treeView);
        angleRightSeeker = (SeekBar) findViewById(R.id.angleRightSeeker);
        angleLeftSeeker = (SeekBar) findViewById(R.id.angleLeftSeeker);
        lengthSeeker = (SeekBar) findViewById(R.id.lengthSeeker);
        levelsSeeker = (SeekBar) findViewById(R.id.levelsSeeker);
        angleRightView = (TextView) findViewById(R.id.angleRightView);
        angleLeftView = (TextView) findViewById(R.id.angleLeftView);
        lengthView = (TextView) findViewById(R.id.lengthView);
        levelsView = (TextView) findViewById(R.id.levelsView);

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

//        //Set line color
//        paint = new Paint();
//        paint.setColor(Color.BLUE);
//        paint.setStrokeWidth(10f);

        //create Bitmap and Canvas
        bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bm);
        bmd = new BitmapDrawable(resources, bm);

        canvas.drawColor(Color.BLACK);
        canvas.translate(width/2, height-1);

        //branch(branchLen);
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

    }

    public void drawTree(){
        canvas.save();
        canvas.scale(scale,scale);
        //canvas.drawColor(Color.HSVToColor(back_hsv));
        canvas.drawColor(Color.WHITE);
        branch(branchLen);
        canvas.restore();
    }

    public void branch(float branchLen){

        //Set line color
        Paint paint = new Paint();
        paint.setStrokeWidth(10f);

        if (colorOption == 1){
            col = branchLen % 800;
        } else {
            col = 200;
        }

        float[] hsv = {(int) col,100,100};
        paint.setColor(Color.HSVToColor(hsv));

//
//        paint = new Paint();
//        paint.setColor(Color.BLUE);
//        paint.setStrokeWidth(10f);

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
                //Add Action
                return true;
            case R.id.tree_color:
                //Add Action
                return true;
            case R.id.spectrum:
                colorOption = 1;
                drawTree();
                return true;
            case R.id.save:
                //Add Action
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

}
