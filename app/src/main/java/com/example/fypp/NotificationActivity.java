package com.example.fypp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NotificationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DatabaseReference reference;
    String userRole,username;
    Intent goTo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize bottomNavigationView first
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottomNavigationView after initialization
        bottomNavigationView.setSelectedItemId(R.id.notification);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle the home action
                        goTo = new Intent(NotificationActivity.this, HomeAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.notification:
                        // Handle the notification action
                        goTo = new Intent(NotificationActivity.this, NotificationActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(NotificationActivity.this, TicketAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(NotificationActivity.this, MessageActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                }
                return true;
            }

            private String getUserPosition() {
                String userPosition = String.valueOf(FirebaseDatabase.getInstance().getReference("users"));
                reference.child(userPosition).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String position = dataSnapshot.child("users").getValue(String.class);
                            // Check the position of the current user
                            if ("SOC Manager".equals(position)) {
                                Intent goToManagerHome = new Intent(NotificationActivity.this, HomeManagerActivity.class);
                                startActivity(goToManagerHome);
                            } else if ("SOC Analyst".equals(position)) {
                                Intent goToAnalystHome = new Intent(NotificationActivity.this, HomeAnalystActivity.class);
                                startActivity(goToAnalystHome);
                            }
                            return;
                        } else {
                            // Handle the case where the user data does not exist
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NotificationActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        });

    }
}