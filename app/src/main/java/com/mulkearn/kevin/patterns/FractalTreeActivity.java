package com.mulkearn.kevin.patterns;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FractalTreeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SeekBar angleRightSeeker, angleLeftSeeker, lengthSeeker, levelsSeeker, backColorSeeker, treeColorSeeker;
    TextView valueView;
    LinearLayout treeView;
    Resources resources;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;

    public  static final int RequestPermissionCode  = 1 ;
    int width, height;
    float angleRight = 30f, angleLeft = -30f, branchLen = 400, decayLength = 0.67f, levels = 80f, thickness = 10f;
    int backHue = -1, treeHue = -1;
    String mCurrentPhotoPath;

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
        valueView = (TextView) findViewById(R.id.valueView);

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

        EnableRuntimePermission();

        //Right angle
        angleRightSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleRight = progress;
                valueView.setText("" + (int) angleRight);
                drawTree();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Left angle
        angleLeftSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleLeft = -progress;
                valueView.setText("" + (int) angleLeft);
                drawTree();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Initial Branch Length
        lengthSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                branchLen = progress;
                valueView.setText("" + (int) branchLen);
                drawTree();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Level of branches
        levelsSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                levels = mapValuesFloat(progress, 0, 100, branchLen, 10);
                valueView.setText("");
                drawTree();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Color of tree
        backColorSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backHue = progress;
                drawTree();
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
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                treeColorSeeker.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        int rAngVis = angleRightSeeker.getVisibility();
        int lAngVis = angleLeftSeeker.getVisibility();
        int lenVis = lengthSeeker.getVisibility();
        int levVis = levelsSeeker.getVisibility();
        String value = valueView.getText().toString();
        outState.putInt("rAngVis", rAngVis);
        outState.putInt("lAngVis", lAngVis);
        outState.putInt("lenVis", lenVis);
        outState.putInt("levVis", levVis);
        outState.putString("value", value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        // 0 = visible, 1 = invisible
        int rAngVis = savedInstanceState.getInt("rAngVis", 0);
        int lAngVis = savedInstanceState.getInt("lAngVis", 1);
        int lenVis = savedInstanceState.getInt("lenVis", 1);
        int levVis = savedInstanceState.getInt("levVis", 1);
        String value = savedInstanceState.getString("value");
        angleRightSeeker.setVisibility(rAngVis);
        angleLeftSeeker.setVisibility(lAngVis);
        lengthSeeker.setVisibility(lenVis);
        levelsSeeker.setVisibility(levVis);
        valueView.setText(value);
    }

    public void drawTree(){
        canvas.save();
        if (backHue == -1){
            canvas.drawColor(Color.WHITE);
        } else {
            float[] back_hsv = {backHue, 100, 100};
            canvas.drawColor(Color.HSVToColor(back_hsv));
        }
        branch(branchLen, thickness);
        canvas.restore();
    }

    public void branch(float branchLen, float thickness){
        //Set line color
        Paint paint = new Paint();
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
        paint.setStrokeWidth(thickness);
        canvas.drawLine(0, 0, 0, -branchLen, paint);
        canvas.translate(0, -branchLen);
        if (branchLen > levels){
            canvas.save();
            canvas.rotate(angleRight);
            branch(branchLen*decayLength, thickness*0.9f);
            canvas.restore();
            canvas.save();
            canvas.rotate(angleLeft);
            branch(branchLen*decayLength, thickness*0.9f);
            canvas.restore();
        }
        //Draw lines
        treeView.setBackground(bmd);
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
                saveImage(bmd);
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
        } else if (id == R.id.home) {
            Intent i_home = new Intent(this, MainActivity.class);
            startActivity(i_home);
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

    public  void saveImage(BitmapDrawable bmd){
        //get bitmap
        Bitmap bitmap = bmd.getBitmap();
        //Create unique name
        String timeStamp = new SimpleDateFormat("ddMMyy_HHmmss").format(new Date());
        String imageFileName = "FT_" + timeStamp;
        // Create a path where we will place our pictures
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File filePath = new File(path + "/PatternsImages");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();
            // Create file
            File file = new File(filePath, imageFileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Image Saved", Toast.LENGTH_LONG).show();
            // Tell the media scanner about the new file so that it is immediately available.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (IOException e) {
            // Unable to create file
            e.printStackTrace();
            Toast.makeText(this, "Error While Saving", Toast.LENGTH_LONG).show();
        }
    }

//    private void galleryAddPic(File f) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    public void displayRightSeeker(View view) {
        valueView.setText("" + (int) angleRight);
        angleRightSeeker.setVisibility(View.VISIBLE);
        angleLeftSeeker.setVisibility(View.INVISIBLE);
        lengthSeeker.setVisibility(View.INVISIBLE);
        levelsSeeker.setVisibility(View.INVISIBLE);
    }

    public void displayLeftSeeker(View view) {
        valueView.setText("" + (int) angleLeft);
        angleRightSeeker.setVisibility(View.INVISIBLE);
        angleLeftSeeker.setVisibility(View.VISIBLE);
        lengthSeeker.setVisibility(View.INVISIBLE);
        levelsSeeker.setVisibility(View.INVISIBLE);
    }

    public void displayLengthSeeker(View view) {
        valueView.setText("" + (int) branchLen);
        angleRightSeeker.setVisibility(View.INVISIBLE);
        angleLeftSeeker.setVisibility(View.INVISIBLE);
        lengthSeeker.setVisibility(View.VISIBLE);
        levelsSeeker.setVisibility(View.INVISIBLE);
    }

    public void displayLevelsSeeker(View view) {
        valueView.setText("");
        angleRightSeeker.setVisibility(View.INVISIBLE);
        angleLeftSeeker.setVisibility(View.INVISIBLE);
        lengthSeeker.setVisibility(View.INVISIBLE);
        levelsSeeker.setVisibility(View.VISIBLE);
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Toast.makeText(FractalTreeActivity.this,"Save permission allowed", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
    }
}
