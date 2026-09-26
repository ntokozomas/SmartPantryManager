package com.ntokozo.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point of the app. For now it only shows a placeholder layout;
 * this will become the Pantry List screen.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
