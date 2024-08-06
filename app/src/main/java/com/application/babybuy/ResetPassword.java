package com.application.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private androidx.appcompat.widget.AppCompatButton reset_password_btn;
    private androidx.appcompat.widget.AppCompatEditText email_input;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset_activity);

        mAuth = FirebaseAuth.getInstance();



        email_input = findViewById(R.id.email_input);
        reset_password_btn = findViewById(R.id.reset_password_btn);
        reset_password_btn.setOnClickListener( v -> {
            userPasswordReset();
        });
    }
    private void userPasswordReset() {
        String email = email_input.getText().toString().trim();

        if (email.isEmpty()) {
            email_input.setError("Email address is required!");
            email_input.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.setError("Please provide a valid email address!");
            email_input.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                    Toast.makeText(ResetPassword.this, "Please check your email for for password rest link", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ResetPassword.this, "Invalid email! please provide a valid email address !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}