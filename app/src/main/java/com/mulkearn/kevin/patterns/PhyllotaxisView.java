package com.mulkearn.kevin.patterns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PhyllotaxisView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PhyllotaxisActivity(this));
    }
}
