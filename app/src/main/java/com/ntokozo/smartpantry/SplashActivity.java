package com.ntokozo.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 🌸 Splash screen: the first screen the user sees (launcher Activity).
 * Plays a short, cute intro animation, then opens the pantry list.
 * Tapping anywhere skips straight to the app.
 */
public class SplashActivity extends AppCompatActivity {

    /** How long the splash stays on screen (milliseconds). */
    private static final long SPLASH_DURATION_MS = 2200;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openPantry = this::openPantry;
    private boolean hasNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View emoji = findViewById(R.id.textSplashEmoji);
        View title = findViewById(R.id.textSplashTitle);
        View tagline = findViewById(R.id.textSplashTagline);
        View foods = findViewById(R.id.textSplashFoods);

        // Start hidden, then animate in one after another
        emoji.setScaleX(0f);
        emoji.setScaleY(0f);
        for (View view : new View[]{title, tagline, foods}) {
            view.setAlpha(0f);
            view.setTranslationY(40f);
        }

        // Basket "pops" in with a little bounce
        emoji.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(2.5f))
                .start();

        fadeUp(title, 350);
        fadeUp(tagline, 600);
        fadeUp(foods, 850);

        findViewById(R.id.splashRoot).setOnClickListener(v -> openPantry());
        handler.postDelayed(openPantry, SPLASH_DURATION_MS);
    }

    private void fadeUp(View view, long delayMs) {
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(500)
                .start();
    }

    /** Opens the pantry list and closes the splash so "back" doesn't return here. */
    private void openPantry() {
        if (hasNavigated) {
            return;
        }
        hasNavigated = true;
        handler.removeCallbacks(openPantry);
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /** If the user leaves during the splash, cancel the pending timer. */
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(openPantry);
        super.onDestroy();
    }
}
