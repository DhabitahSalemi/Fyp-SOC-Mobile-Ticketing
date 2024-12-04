package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicketAnalystActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<TicketData> list;
    Button assignedButton, escalationButton;
    String userRole,username; // Declare userRole variable
    BottomNavigationView bottomNavigationView;
    DatabaseReference reference, assignedReference, escalationReference;
    TicketAdapter adapter;
    EscalationTicketAdapter escalationAdapter;
    Intent goTo;
    int assignedTicketCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_analyst);

        // Initialize bottomNavigationView first
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottomNavigationView after initialization
        bottomNavigationView.setSelectedItemId(R.id.ticket);

        // Set click listeners for filter buttons
        assignedButton = findViewById(R.id.Assigned_button);
        escalationButton = findViewById(R.id.Escalation_button);

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

        reference = FirebaseDatabase.getInstance().getReference("ticket");
        assignedReference = FirebaseDatabase.getInstance().getReference().child("assign");
        escalationReference = FirebaseDatabase.getInstance().getReference().child("escalation");

        adapter = new TicketAdapter(this, list, picList,userRole, username); //all open ticket
        escalationAdapter = new EscalationTicketAdapter(this, list, picList,userRole, username);

        //display default list (all open ticket)
        recyclerView.setAdapter(adapter);

        assignedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole != null && !userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            assignedTicketCount = 0;

                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                //have filter for open only
                                if(ticket != null && !ticketSnapshot.hasChild("closedStatus")) {

                                    // Check if the ticket ID exists in the "assign" table
                                    assignedReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String assignedUsername = dataSnapshot.child("ticketPIC").getValue(String.class);
                                            if (dataSnapshot.exists() && assignedUsername.equals(username)) {
                                                // If data exists in "assign", add this ticket data to the list
                                                assignedTicketCount++;
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

        escalationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole != null && !userRole.equals("Manager")) {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                                TicketData ticket = ticketSnapshot.getValue(TicketData.class);
                                String ticketId = ticket.getTicketid();

                                if(ticket != null && !ticketSnapshot.hasChild("closedStatus")) { //have filter for open only
                                    // Check if the ticket ID exists in the "escalation" table
                                    escalationReference.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String escalatedUsername = dataSnapshot.child("escalatedTo").getValue(String.class);
                                            if (dataSnapshot.exists() && escalatedUsername != null && escalatedUsername.equals(username)) {
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
                    recyclerView.setAdapter(escalationAdapter);
                }//if manager
            }
        });



        // Set click listener for FloatingActionButton
        FloatingActionButton add_button = findViewById(R.id.btn_add);
        add_button.setOnClickListener(view -> {
            // Create a new dialog
            Dialog dialog = new Dialog(TicketAnalystActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.create_ticket_workflow_dialog);

            // Set the dialog window's background to the custom drawable
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

            // button in the dialog layout
            Button createTicketButton = dialog.findViewById(R.id.create_ticket_button);
            createTicketButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent form = new Intent(TicketAnalystActivity.this, TicketForm.class);
                    form.putExtra("username", username);
                    form.putExtra("userRole", userRole);
                    startActivity(form);
                    dialog.dismiss();
                }
            });
            dialog.show(); // Display the dialog
        });

        // Set up BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        goTo = new Intent(TicketAnalystActivity.this, HomeAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.notification:
                        // Handle the notification action
                        goTo = new Intent(TicketAnalystActivity.this, NotificationActivity.class);
                        startActivity(goTo);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        break;
                    case R.id.ticket:
                        // Handle the ticket action
                        goTo = new Intent(TicketAnalystActivity.this, TicketAnalystActivity.class);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole); //TO-DO (add more stuff to goTo)
                        startActivity(goTo);
                        break;
                    case R.id.message:
                        // Handle the message action
                        goTo = new Intent(TicketAnalystActivity.this, MessageActivity.class);
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