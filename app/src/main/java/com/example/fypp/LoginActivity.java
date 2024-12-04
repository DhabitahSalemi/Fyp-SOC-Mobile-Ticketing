package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView signupRedirectText;
    EditText loginUsername, loginPassword;
    Button loginButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    String positionFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() || !validatePassword()) {
                    // This block can be empty because validation methods handle errors
                } else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(view -> {
            Intent SignupPage = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(SignupPage);
        });
    }

    private boolean userIsManager(DataSnapshot dataSnapshot) {
        positionFromDB = dataSnapshot.child("position").getValue(String.class);
        return positionFromDB != null && positionFromDB.equals("Manager");
    }

    private Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        reference = FirebaseDatabase.getInstance().getReference("users");//table
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);//child == sumthing me input

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = dataSnapshot.child("password").getValue(String.class);

                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            // Passwords match, allow login
                            loginPassword.setError(null);

                            Intent mainPage;
                            if (userIsManager(dataSnapshot)) {
                                mainPage = new Intent(LoginActivity.this, HomeManagerActivity.class);
                            } else {
                                mainPage = new Intent(LoginActivity.this, HomeAnalystActivity.class);
                            }
                            // Pass the username to the next activity
                            mainPage.putExtra("userRole", positionFromDB);
                            mainPage.putExtra("username", userUsername);
                            startActivity(mainPage);
                            return;
                        }
                    }
                    // If none of the passwords match
                    loginPassword.setError("Invalid Credentials");
                    loginPassword.requestFocus();
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
                Toast.makeText(LoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}