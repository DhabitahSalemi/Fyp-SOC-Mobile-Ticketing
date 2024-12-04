package com.example.fypp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessageActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    DatabaseReference reference;
    Intent goTo;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.search_recylerview);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up bottomNavigationView after initialization
        bottomNavigationView.setSelectedItemId(R.id.message);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        searchInput.requestFocus();

        // Initialize Firebase Database
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchInput.getText().toString().trim();
                if(searchTerm.isEmpty()){
                    searchInput.setError("Invalid Username");
                    return;
                }
                // Get users from search term in the database
                setupSearchRecyclerView(searchTerm);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle the home action
                        getUserPosition();
                        return true;
                    case R.id.notification:
                        // Handle the notification action
                        Intent goTo = new Intent(MessageActivity.this, NotificationActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(MessageActivity.this, TicketAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(MessageActivity.this, MessageActivity.class);
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
                                Intent goToManagerHome = new Intent(MessageActivity.this, HomeManagerActivity.class);
                                startActivity(goToManagerHome);
                            } else if ("SOC Analyst".equals(position)) {
                                Intent goToAnalystHome = new Intent(MessageActivity.this, HomeAnalystActivity.class);
                                startActivity(goToAnalystHome);
                            }
                            return;
                        } else {
                            // Handle the case where the user data does not exist
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MessageActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        });
    }


    void setupSearchRecyclerView(String searchTerm){
        // Clear previous data
        recyclerView.setAdapter(null);

        // Firebase query to search for users
        Query query = reference.orderByChild("username").equalTo(searchTerm);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a list to hold users
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract user data
                    // Extract username from each user data
                    String username = snapshot.child("username").getValue(String.class);
                    User user = new User(username); // Create a User object
                    userList.add(user);
                }

                // Initialize RecyclerView adapter with the list of users
                UserAdapter userAdapter = new UserAdapter(userList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });
    }


}