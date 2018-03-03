package com.mulkearn.kevin.patterns;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class PhyllotaxisView extends AppCompatActivity {

    private PhyllotaxisActivity customView;
    int c = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phyllotaxis);
        //setContentView(new PhyllotaxisActivity(this));

        //customView = new PhyllotaxisActivity();

        SeekBar c_seeker = (SeekBar) findViewById(R.id.c_seeker);

        c_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c = progress;
                //customView.setValues(c);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }
}
