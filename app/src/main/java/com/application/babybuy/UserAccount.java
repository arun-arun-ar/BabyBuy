package com.application.babybuy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserAccount extends AppCompatActivity {

    private EditText username_input, password_input, confirm_password_input;
    private Button save_btn, delete_btn;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account_activity);

        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        confirm_password_input = findViewById(R.id.confirm_password_input);

        save_btn = findViewById(R.id.save_btn);
        delete_btn = findViewById(R.id.delete_btn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();
        final String[] userName = new String[1];

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserActivity userActivityProfile = snapshot.getValue(UserActivity.class);
                if (userActivityProfile != null) {
                    userName[0] = userActivityProfile.userName;
                    username_input.setText(userName[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserAccount.this, "Error in loading user", Toast.LENGTH_SHORT).show();
            }
        });

        save_btn.setOnClickListener(view -> {

            if (username_input.getText().toString() != userName[0]) {
                saveUserName();
            }

            if (!password_input.getText().toString().isEmpty()) {
                saveUserPassword();
            }
        });
        delete_btn.setOnClickListener(v -> {
            deleteUser(userName[0]);
        });
    }

    private void deleteUser(String userName) {
        String username = userName;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Are you sure you want to delete your account?");
        alert.setMessage(username);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserAccount.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                finish();

                startActivity(new Intent(UserAccount.this, loginActivity.class));
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    private void saveUserName() {
        String username = username_input.getText().toString().trim();

        if (username.isEmpty() != true) {
            if (user != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userName", username);
                reference.child(user.getUid()).updateChildren(hashMap);
                Toast.makeText(UserAccount.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
            }
        } else {
            username_input.setError(" Username Required!");
            username_input.requestFocus();
            return;
        }
    }

    private void saveUserPassword() {
        String password = password_input.getText().toString().trim();
        String confirmPassword = confirm_password_input.getText().toString().trim();

        if (!password.isEmpty()) {
            if (!confirmPassword.isEmpty()) {
                if (!confirmPassword.equals(password)) {
                    password_input.setError("Sorry! password didn't not match");
                    password_input.requestFocus();
                    return;
                }
                user.updatePassword(password)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserAccount.this, "Password updated successfully !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                confirm_password_input.setError("Please conform your password!");
                confirm_password_input.requestFocus();
                return;
            }

        }

    }
}
