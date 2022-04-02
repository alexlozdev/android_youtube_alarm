package com.gu.galarm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.gu.galarm.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private static final int SPLASH_DELAY_TIME = 1500;

    private boolean mUserRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mUserRegistered = false;


        //final boolean bLocal = checkLocalRegister(this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startMainActivity();

            }
        }, SPLASH_DELAY_TIME);


    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity() {

    }

}
