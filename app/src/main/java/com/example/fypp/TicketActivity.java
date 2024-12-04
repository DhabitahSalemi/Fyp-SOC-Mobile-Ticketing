package com.example.fypp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicketActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<TicketData> list;
    Button newButton, escalationButton, reportButton, closedButton;
    String userRole,username; // Declare userRole variable
    BottomNavigationView bottomNavigationView;
    DatabaseReference reference,assignedReference, reportReference;
    TicketAdapter adapter;
    AssignTicketAdapter assignAdapter;
    ReportTicketAdapter reportAdapter;
    ClosedTicketAdapter closedAdapter;
    Intent goTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // Initialize bottomNavigationView first
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottomNavigationView after initialization
        bottomNavigationView.setSelectedItemId(R.id.ticket);

        // Set click listeners for filter buttons
        newButton = findViewById(R.id.New_button);
        escalationButton = findViewById(R.id.Assigned_button);
        reportButton = findViewById(R.id.Report_button);
        closedButton = findViewById(R.id.Closed_button);

        // Initialize views and adapters
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        ArrayList<TicketAssignData> picList = new ArrayList<>(); // Initialize picList

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

        // Initialize Firebase reference
        reference = FirebaseDatabase.getInstance().getReference("ticket");
        assignedReference = FirebaseDatabase.getInstance().getReference().child("assign");
        reportReference = FirebaseDatabase.getInstance().getReference("report");

        //initialize adapter
        adapter = new TicketAdapter(this, list, picList,userRole, username); //all open ticket
        assignAdapter = new AssignTicketAdapter(this, list, picList,userRole,username); //all assinged open ticket
        reportAdapter = new ReportTicketAdapter(this, list, picList,userRole, username); //all reported open ticket
        closedAdapter = new ClosedTicketAdapter(this, list, picList,userRole, username);

        if (userRole != null && userRole.equals("Manager")) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TicketData ticket = dataSnapshot.getValue(TicketData.class);
                        if (ticket != null && !dataSnapshot.hasChild("closedStatus")) { //have filter for open only
                            list.add(ticket);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            }); //all open assigned ticket
        }

        //display default list (all open ticket)
        recyclerView.setAdapter(adapter);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle newButton actions
                if (userRole != null && userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                if(ticket != null && !ticketSnapshot.hasChild("closedStatus")) { //have filter for open only
                                    // Check if the ticket ID exists in the "assign" table
                                    assignedReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // If data exists in "assign", do nothing
                                            } else {
                                                // If data doesn't exist in "assign", add this ticket data to the list
                                                list.add(ticket);
                                                assignAdapter.notifyDataSetChanged();
                                            }}
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }}}
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    recyclerView.setAdapter(assignAdapter);
                }}});

        escalationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRole != null && userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                if(ticket != null && !ticketSnapshot.hasChild("closedStatus")) { //have filter for open only
                                    // Check if the ticket ID exists in the "assign" table
                                    assignedReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // If data exists in "assign", add this ticket data to the list
                                                list.add(ticket);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Handle onCancelled
                                        }
                                    });
                                }//if

                            }//for
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }//if manager
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole != null && userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                if(ticket != null && !ticketSnapshot.hasChild("closedStatus")) { //have filter for open only
                                    // Check if the ticket ID exists in the "report" table
                                    reportReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // If data exists in "report", add this ticket data to the list
                                                list.add(ticket);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Handle onCancelled
                                        }
                                    });
                                }//if

                            }//for
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                    recyclerView.setAdapter(reportAdapter);
                }//if manager
            }
        });

        closedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole != null && userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                if(ticket != null && ticketSnapshot.hasChild("closedStatus")) { //have filter for open only
                                    // Check if the ticket ID exists in the "report" table
                                    reference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // If data exists in "report", add this ticket data to the list
                                                list.add(ticket);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Handle onCancelled
                                        }
                                    });
                                }//if

                            }//for
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                    recyclerView.setAdapter(closedAdapter);
                }
            }
        });

        // Set up BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        goTo = new Intent(TicketActivity.this, HomeManagerActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.notification:
                        // Handle the notification action
                        goTo = new Intent(TicketActivity.this, NotificationActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(TicketActivity.this, TicketActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(TicketActivity.this, MessageActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        break;
                }
                return true;
            }

        });
    }
}