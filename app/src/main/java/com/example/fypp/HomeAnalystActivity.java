package com.example.fypp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeAnalystActivity extends AppCompatActivity {
    String userRole,username;
    TextView newTicketCountTv, assignedTicketCountTv, allTicketCountTv, closedTicketCountTv;
    Intent goTo;
    DatabaseReference reference, assignedReference, reportReference;
    int newTicketCount = 0;
    int assignedTicketCount = 0;
    int allTicketCount = 0;
    int closedTicketCount = 0;

    // Add new variables for analytics
    private TextView avgResolutionTimeTv;
    private TextView pendingEscalationsTv;
    private TextView criticalTicketsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_analyst);

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

        // Initialize new TextViews for analytics
        avgResolutionTimeTv = findViewById(R.id.avgResolutionTime);
        pendingEscalationsTv = findViewById(R.id.pendingEscalations);
        criticalTicketsTv = findViewById(R.id.criticalTickets);

        // Get the username from intent
        username = getIntent().getStringExtra("username");

        // Initialize Firebase references
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        assignedReference = FirebaseDatabase.getInstance().getReference().child("assign");
        reportReference = FirebaseDatabase.getInstance().getReference("report");

        // Modify the Firebase listener to track additional metrics
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Reset counters
                newTicketCount = 0;
                assignedTicketCount = 0;
                allTicketCount = 0;
                closedTicketCount = 0;
                int criticalCount = 0;
                int pendingEscalations = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TicketData ticket = dataSnapshot.getValue(TicketData.class);
                    if (ticket != null) {
                        allTicketCount++;
                        
                        // Check ticket priority for critical tickets
                        if (ticket.getTicketpriority().equals("1")) {
                            criticalCount++;
                        }

                        // Track ticket status
                        if (!dataSnapshot.hasChild("closedStatus")) {
                            newTicketCount++;
                        } else {
                            closedTicketCount++;
                        }

                        // Check for pending escalations
                        String ticketId = ticket.getTicketid();
                        checkEscalationStatus(ticketId, pendingEscalations);
                        
                        // Check assigned tickets
                        checkAssignedTickets(ticketId, username);
                    }
                }
                
                // Update UI with new metrics
                updateExtendedTicketCounts(criticalCount, pendingEscalations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeAnalystActivity.this, "Failed to load metrics", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize bottomNavigationView first
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottomNavigationView after initialization
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle the home action
                        goTo = new Intent(HomeAnalystActivity.this, HomeAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.notification:
                        // Handle the notification action
                        goTo = new Intent(HomeAnalystActivity.this, NotificationActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(HomeAnalystActivity.this, TicketAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(HomeAnalystActivity.this, MessageActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                }
                return true;
            }

            private void getUserPosition() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                String userPosition = String.valueOf(reference);
                reference.child(userPosition).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String position = dataSnapshot.child("users").getValue(String.class);
                            // Check the position of the current user
                            if ("SOC Manager".equals(position)) {
                                Intent goToManagerHome = new Intent(HomeAnalystActivity.this, HomeManagerActivity.class);
                                startActivity(goToManagerHome);
                            } else if ("SOC Analyst".equals(position)) {
                                // No need to navigate to the same activity
                            }
                        } else {
                            // Handle the case where the user data does not exist
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeAnalystActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getFCMToken();
    }

    // Method to update the TextViews with the counts
    private void updateTicketCounts() {
        newTicketCountTv.setText(String.valueOf(newTicketCount));
        assignedTicketCountTv.setText(String.valueOf(assignedTicketCount));
        allTicketCountTv.setText(String.valueOf(allTicketCount));
        closedTicketCountTv.setText(String.valueOf(closedTicketCount));
    }

    // To allow user to receive notification
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    Log.i("My Token",token);
                }
            }
        });
    }

    // New helper methods
    private void checkEscalationStatus(String ticketId, int pendingCount) {
        DatabaseReference escalationRef = FirebaseDatabase.getInstance().getReference("escalation").child(ticketId);
        escalationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !snapshot.hasChild("resolved")) {
                    pendingCount++;
                    pendingEscalationsTv.setText(String.valueOf(pendingCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void checkAssignedTickets(String ticketId, String username) {
        assignedReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String assignedUsername = dataSnapshot.child("ticketPIC").getValue(String.class);
                if (dataSnapshot.exists() && assignedUsername.equals(username)) {
                    if (!dataSnapshot.hasChild("closedStatus")) {
                        assignedTicketCount++;
                        updateTicketCounts();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateExtendedTicketCounts(int criticalCount, int pendingEscalations) {
        // Update existing counts
        updateTicketCounts();
        
        // Update new metrics
        criticalTicketsTv.setText(String.valueOf(criticalCount));
        pendingEscalationsTv.setText(String.valueOf(pendingEscalations));
        
        // Calculate and update average resolution time
        calculateAverageResolutionTime();
    }

    private void calculateAverageResolutionTime() {
        reportReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalTime = 0;
                int closedTickets = 0;
                
                for (DataSnapshot ticketSnapshot : snapshot.getChildren()) {
                    if (ticketSnapshot.hasChild("closedTime") && ticketSnapshot.hasChild("createdTime")) {
                        long closeTime = ticketSnapshot.child("closedTime").getValue(Long.class);
                        long createTime = ticketSnapshot.child("createdTime").getValue(Long.class);
                        totalTime += (closeTime - createTime);
                        closedTickets++;
                    }
                }
                
                if (closedTickets > 0) {
                    long avgTime = totalTime / closedTickets;
                    // Convert to hours and update UI
                    String avgTimeStr = String.format("%.1f hrs", avgTime / (1000.0 * 60 * 60));
                    avgResolutionTimeTv.setText(avgTimeStr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}