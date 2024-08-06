package com.application.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private androidx.appcompat.widget.AppCompatEditText username_input, email_input, password_input, confirm_password_input;
    private androidx.appcompat.widget.AppCompatButton register_btn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babybuy_registration_activity);

        mAuth = FirebaseAuth.getInstance();

        // registration fields
        username_input = findViewById(R.id.username_input);
        email_input = findViewById(R.id.email_input);
        password_input =  findViewById(R.id.password_input);
        confirm_password_input = findViewById(R.id.confirm_password_input);


        TextView sign_in_label = findViewById(R.id.sign_in_label);
        sign_in_label.setOnClickListener(v -> {
            this.finish();
            startActivity(new Intent(Registration.this, loginActivity.class));
        });

        // register button
        register_btn =  findViewById(R.id.register_btn);
        register_btn.setOnClickListener(v -> {
            userRegister();
        });
    }


    private void userRegister() {
        String username = username_input.getText().toString().trim();
        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();
        String confirmPassword = confirm_password_input.getText().toString().trim();

        // check if username is empty or not
        if (username.isEmpty()) {
            username_input.setError("Please provide a username.!");
            username_input.requestFocus();
            return;
        }


        // check if email is empty or not
        if (email.isEmpty()) {
            email_input.setError("Please provide a valid email address!");
            email_input.requestFocus();
            return;

            // check email is valid or not
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.setError("Invalid email address! Please provide a valid email address.");
            email_input.requestFocus();
            return;
        }


        // check if password field is empty or not
        if (password.isEmpty()) {
            password_input.setError("Please provide a email address.");
            password_input.requestFocus();
            return;
        }

        // password length validation
        if (password.length() < 8) {
            password_input.setError(" Your password must contain at least 8 characters.");
            password_input.requestFocus();
            return;
        }

        // conform password validation
        if(!confirmPassword.equals(password)){
            password_input.setError("Password didn't match! Please check your password.");
            password_input.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registration.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    UserActivity userActivity = new UserActivity(username, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid())
                            .setValue(userActivity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registration.this, "User has been registered successfully.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(Registration.this, "Something error happened! Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Registration.this, "Sorry! User already exists. Please Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}