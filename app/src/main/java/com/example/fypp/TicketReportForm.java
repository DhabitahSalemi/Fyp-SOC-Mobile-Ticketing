package com.example.fypp;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketReportForm extends AppCompatActivity {
    EditText actionPlan;
    FirebaseDatabase database;
    DatabaseReference reference;
    AutoCompleteTextView userList;
    ArrayAdapter<String> adapterItems;
    List<String> items;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_report_form);

        // Initialize items list
        items = new ArrayList<>();

        // Get the Intent that started this activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        // Retrieve data from the Intent
        String ticketId = intent.getStringExtra("ticketId");

        TextView ticketIdDetail = findViewById(R.id.ticketid_detail);
        ticketIdDetail.setText(ticketId);

        userList = findViewById(R.id.reportedby_list);

        // Fetch user names from the database
        fetchUserNamesFromDatabase();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Selected item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // Check if the user's input is empty
        /*String reportedBy = intent.getStringExtra("reportedBy");
        if (reportedBy.isEmpty()) {
            Toast.makeText(TicketReportForm.this, "Please select a user", Toast.LENGTH_SHORT).show();
            return;
        }*/

        actionPlan = findViewById(R.id.actionPlan_detail);

        // Set the text of reasons EditText based on the Intent data
        String actionPlanText = intent.getStringExtra("actionPlan");
        if (actionPlan != null) {
            actionPlan.setText(actionPlanText);
        }
        // submitRequest Button
        Button submitReportButton = findViewById(R.id.submitReport_button);

        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("report");

                String ticketId = ticketIdDetail.getText().toString(); // Retrieve ticket ID
                String reportedBy = userList.getText().toString();
                String actionPlanText = actionPlan.getText().toString();

                // Get the selected user's name
                //String reportedBy = userList.getText().toString();

                // Ensure a user is selected
                if (reportedBy.isEmpty()) {
                    Toast.makeText(TicketReportForm.this, "Please select a user", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the selected user's name from the AutoCompleteTextView
                //String reportedBy = userList.getText().toString();

                TicketReportData ticketReportData = new TicketReportData(actionPlanText);

                // Create a HashMap to hold both the action plan and the user who reported it
                HashMap<String, Object> reportDetails = new HashMap<>();
                reportDetails.put("reported by", reportedBy); // Include the user who reported the issue
                reportDetails.put("action plan", actionPlanText);

                // Save the report along with the user's name in the database
                //reference.child(ticketId).child("by").setValue(reportedBy);
                /*reference.child(ticketId).child("action plan").setValue(actionPlanText)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent goTo = new Intent(TicketReportForm.this, TicketActivity.class);
                                goTo.putExtra("action plan",actionPlanText);
                                startActivity(goTo);
                            }
                        });*/

                // Set the report details in the database
                reference.child(ticketId).setValue(reportDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent goTo = new Intent(TicketReportForm.this, TicketAnalystActivity.class);
                                    goTo.putExtra("action plan", actionPlanText);
                                    goTo.putExtra("username", username);
                                    goTo.putExtra("userRole", userRole);
                                    startActivity(goTo);
                                } else {
                                    Toast.makeText(TicketReportForm.this, "Failed to submit report: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void fetchUserNamesFromDatabase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userName = snapshot.child("username").getValue(String.class);
                    if (userName != null) {
                        userNames.add(userName);
                    }
                }
                // Once you have the user names, populate the adapter
                adapterItems = new ArrayAdapter<>(TicketReportForm.this, R.layout.option_list, userNames);

                // Set the adapter to the AutoCompleteTextView
                userList.setAdapter(adapterItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TicketReportForm.this, "Failed to fetch user names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private TicketData findTicketDataById(String ticketId) {
        for (TicketData ticket : list) {
            if (ticket.getTicketid().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }*/

}