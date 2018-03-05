package com.mulkearn.kevin.patterns;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhyllotaxisActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout phyloView;
    SeekBar c_seeker, max_seeker, size_seeker, angle_seeker;
    TextView valueView;

    int width, height;
    Resources resources;
    Canvas canvas;
    Bitmap bm;
    BitmapDrawable bmd;
    String mCurrentPhotoPath;

    int c = 20;
    int n = 0;
    int max = 1000;
    int size = 10;
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
        valueView = (TextView) findViewById(R.id.valueView);


        //Toolbar and drawer setup
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

        //Draw in the centre of the screen
        canvas.translate(width/2, height/2);

        drawPhyllo();

        c_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c = progress;
                valueView.setText("" + c);
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        max_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                max = progress + 1;
                valueView.setText("" + max);
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        size_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                valueView.setText("" + size);
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        angle_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp = progress;
                angle = mapValuesDouble((double) temp, 0, 100, 137.0, 138.0);
                valueView.setText("" + angle);
                drawPhyllo();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        int cVis = c_seeker.getVisibility();
        int maxVis = max_seeker.getVisibility();
        int sizeVis = size_seeker.getVisibility();
        int angVis = angle_seeker.getVisibility();
        String value = valueView.getText().toString();
        outState.putInt("cVis", cVis);
        outState.putInt("maxVis", maxVis);
        outState.putInt("sizeVis", sizeVis);
        outState.putInt("angVis", angVis);
        outState.putString("value", value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        // 0 = visible, 1 = invisible
        int cVis = savedInstanceState.getInt("cVis", 0);
        int maxVis = savedInstanceState.getInt("maxVis", 1);
        int sizeVis = savedInstanceState.getInt("sizeVis", 1);
        int angVis = savedInstanceState.getInt("angVis", 1);
        String value = savedInstanceState.getString("value");
        c_seeker.setVisibility(cVis);
        max_seeker.setVisibility(maxVis);
        size_seeker.setVisibility(sizeVis);
        angle_seeker.setVisibility(angVis);
        valueView.setText(value);
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
            case R.id.save:
                saveImage(bmd);
                return true;
            case R.id.reset:
                Intent i_reset = new Intent(this, PhyllotaxisActivity.class);
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

    public  void saveImage(BitmapDrawable bmd){
        // Get the bitmap from drawable object
        Bitmap bitmap = bmd.getBitmap();
        try {
            File file = createImageFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Image Saved", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Error While Saving", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyy_HHmmss").format(new Date());
        String imageFileName = "Phyllo_" + timeStamp;
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File storageDir = new File(root + "/PatternsImages"); //Save location
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void displayCSeeker(View view) {
        valueView.setText("" + c);
        c_seeker.setVisibility(View.VISIBLE);
        max_seeker.setVisibility(View.INVISIBLE);
        size_seeker.setVisibility(View.INVISIBLE);
        angle_seeker.setVisibility(View.INVISIBLE);
    }

    public void displayMaxSeeker(View view) {
        valueView.setText("" + max);
        c_seeker.setVisibility(View.INVISIBLE);
        max_seeker.setVisibility(View.VISIBLE);
        size_seeker.setVisibility(View.INVISIBLE);
        angle_seeker.setVisibility(View.INVISIBLE);
    }

    public void displaySizeSeeker(View view) {
        valueView.setText("" + size);
        c_seeker.setVisibility(View.INVISIBLE);
        max_seeker.setVisibility(View.INVISIBLE);
        size_seeker.setVisibility(View.VISIBLE);
        angle_seeker.setVisibility(View.INVISIBLE);
    }

    public void displayAngleSeeker(View view) {
        valueView.setText("" + angle);
        c_seeker.setVisibility(View.INVISIBLE);
        max_seeker.setVisibility(View.INVISIBLE);
        size_seeker.setVisibility(View.INVISIBLE);
        angle_seeker.setVisibility(View.VISIBLE);
    }
}
