package com.application.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    private androidx.appcompat.widget.AppCompatEditText email_input, password_input;

    private TextView sign_up_label, forgotPass;
    private androidx.appcompat.widget.AppCompatButton sign_in_btn;
    private ProgressBar progres_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babybuy_login_activity);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);

        sign_up_label = findViewById(R.id.sign_up_label);
        sign_up_label.setOnClickListener(this);

        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_in_btn.setOnClickListener(this);

        forgotPass = (TextView) findViewById(R.id.forgotPass);
        forgotPass.setOnClickListener(this);

        progres_bar = findViewById(R.id.progres_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_btn:

                progres_bar.setVisibility(View.VISIBLE);
                sign_in_btn.setClickable(false);

                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(ConnectivityManager.class);

                if (connectivityManager.getActiveNetwork() == null) {
                    Toast.makeText(loginActivity.this, "Pleas check your internet connection.", Toast.LENGTH_SHORT).show();
                    progres_bar.setVisibility(View.GONE);
                    break;
                }
                userLogin();
                break;

            case R.id.forgotPass:
                startActivity(new Intent(this, ResetPassword.class));
                break;


            case R.id.sign_up_label:
                startActivity(new Intent(getApplicationContext(), Registration.class));
                break;

        }
    }

    private void userLogin() {

        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        // check email field if empty
        if (email.isEmpty()) {
            email_input.setError("Please provide a email address!");
            email_input.requestFocus();
            hideSpinner();
            return;
        }

        // email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.setError("Invalid email! Please provide a valid email  address");
            email_input.requestFocus();
            hideSpinner();
            return;
        }

        // password length validation
        if (password.length() < 6) {
            password_input.setError("Your password should contain at least 6 characters.");
            password_input.requestFocus();
            hideSpinner();
            return;
        }

        // to check password field if empty
        if (password.isEmpty()) {
            password_input.setError("Password is required !");
            password_input.requestFocus();
            hideSpinner();
            return;
        }


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("user_login", true).apply();

                        new Handler().postDelayed(() -> {

                            //  to store information of logged in user
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            hideSpinner();
                            finish();

                        }, 1000);
                        return;
                    }

                    hideSpinner();

                    // user validation
                    user.sendEmailVerification();
                    Toast.makeText(loginActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                hideSpinner();
                //  user name and password validation
                Toast.makeText(loginActivity.this, "Invalid credentials! Please provide valid credentials.", Toast.LENGTH_SHORT).show();
                password_input.setError("");
                email_input.setError("");
                return;
            }
        });
    }

    private void hideSpinner(){
        progres_bar.setVisibility(View.GONE);
        sign_in_btn.setClickable(true);
    }

}