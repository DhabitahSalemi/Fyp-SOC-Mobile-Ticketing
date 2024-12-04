package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeManagerActivity extends AppCompatActivity
{
    TextView newTicketCountTv, assignedTicketCountTv, allTicketCountTv, closedTicketCountTv;
    Intent goTo;
    String username, userRole;
    // Declare Firebase references and count variables
    DatabaseReference reference, assignedReference, reportReference;
    int newTicketCount = 0;
    int assignedTicketCount = 0;
    int allTicketCount = 0;
    int closedTicketCount = 0;

    // TextViews for displaying the counts

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);

        //get extra bundle
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        TextView userRoleTv = findViewById(R.id.userRoleTv);
        userRoleTv.setText(userRole);

        TextView usernameTv = findViewById(R.id.usernameTv);
        usernameTv.setText(username);

        // Initialize TextViews
        newTicketCountTv = findViewById(R.id.newTicketCount);
        assignedTicketCountTv = findViewById(R.id.assignedTicketCount);
        allTicketCountTv = findViewById(R.id.allTicketCount);
        closedTicketCountTv = findViewById(R.id.closedTicketCount);

        // Initialize Firebase references
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        assignedReference = FirebaseDatabase.getInstance().getReference().child("assign");
        reportReference = FirebaseDatabase.getInstance().getReference("report");

        // Add ValueEventListener to count the tickets
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newTicketCount = 0;
                assignedTicketCount = 0;
                allTicketCount = 0;
                closedTicketCount = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TicketData ticket = dataSnapshot.getValue(TicketData.class);
                    if (ticket != null) {
                        allTicketCount++;
                        if (!dataSnapshot.hasChild("closedStatus")) {
                            newTicketCount++;
                        } else {
                            closedTicketCount++;
                        }

                        // Check if the ticket ID exists in the "assign" table
                        assignedReference.child(ticket.getTicketid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    assignedTicketCount++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle onCancelled
                            }
                        });
                    }
                }
                updateTicketCounts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle the home action
                        goTo = new Intent(HomeManagerActivity.this, HomeManagerActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.notification:
                        // Handle the notification action
                        Intent goTo = new Intent(HomeManagerActivity.this, NotificationActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(HomeManagerActivity.this, TicketActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(HomeManagerActivity.this, MessageActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                }
                return true;
            }

        });
    }

    // Method to update the TextViews with the counts
    private void updateTicketCounts() {
        newTicketCountTv.setText(String.valueOf(newTicketCount));
        assignedTicketCountTv.setText(String.valueOf(assignedTicketCount));
        allTicketCountTv.setText(String.valueOf(allTicketCount));
        closedTicketCountTv.setText(String.valueOf(closedTicketCount));
    }
}