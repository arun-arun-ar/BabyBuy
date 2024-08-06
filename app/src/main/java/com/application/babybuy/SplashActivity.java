package com.application.babybuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babybuy_splash_activity);


        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("user_login", false);


//      redirect to login if internet not available
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);

        if (connectivityManager.getActiveNetwork() == null) {
            this.finish();
            startActivity(new Intent(SplashActivity.this, loginActivity.class));
            return;
        }


        new Handler().postDelayed(() -> {
            if (check) {

                // check user lgoin
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(SplashActivity.this, StartActivity.class));
            }
            this.finish();
        }, 1000);

    }
}