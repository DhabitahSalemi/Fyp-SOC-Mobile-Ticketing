package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    TextView loginRedirectText;
    EditText signupUsername, signupUserID, signupEmail, signupPassword;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    String[] items = {"Manager","SOC Analyst"};

    AutoCompleteTextView positionList;

    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupUsername = findViewById(R.id.signup_username);
        signupUserID = findViewById(R.id.signup_userid);
        positionList = findViewById(R.id.position_list);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);

        adapterItems = new ArrayAdapter<String>(this,R.layout.option_list,items);
        positionList.setAdapter(adapterItems);

        positionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(view -> {
            // Establish connection to the firebase realtime database
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            // Retrieving the text input from the user interface
            String username = signupUsername.getText().toString();
            String userid = signupUserID.getText().toString();
            String position = positionList.getText().toString();
            String email = signupEmail.getText().toString();
            String password = signupPassword.getText().toString();

            UserData userData = new UserData(email,password,position,userid,username);
            reference.child(username).setValue(userData);

            Toast.makeText(SignupActivity.this, "Sign Up Successful! Redirecting...", Toast.LENGTH_SHORT).show();

            // Direct user to login page
            Intent login = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(login);
        });

        // Direct user to login page when click text
        loginRedirectText.setOnClickListener(view -> {
            Intent login = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(login);
        });
    }
}