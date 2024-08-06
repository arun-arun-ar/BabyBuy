package com.application.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babybuy_start_page_activity);

        Button register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, Registration.class));
        });

        Button sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_in_btn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, loginActivity.class));
        });
    }

}