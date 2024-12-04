package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TicketReportPage2 extends AppCompatActivity {
    TextView ticketIssueDetail,ticketActionDetail;
    String userRole,username;
    FirebaseDatabase database;
    DatabaseReference reference, updateReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_report_page2);

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

        // Retrieve data from the Intent USED FOR  PREVIOUS PAGE
        String ticketId = intent.getStringExtra("ticketId");
        String ticketDateTime = intent.getStringExtra("ticketDateTime");
        String ticketPriority = intent.getStringExtra("ticketPriority");
        String ticketReport = intent.getStringExtra("ticketReport");
        String ticketPIC = intent.getStringExtra("ticketPIC");
        String ticketURL = intent.getStringExtra("ticketURL");
        String ticketEscalation = intent.getStringExtra("ticketEscalation");
        String ticketReportBy = intent.getStringExtra("ticketReportBy");
        String ticketAction = intent.getStringExtra("ticketAction");
        String ticketIssue = intent.getStringExtra("ticketIssue");
        String ticketEscalatedTo = intent.getStringExtra("ticketEscalatedTo");

        String str1;

        if (ticketEscalation != null) str1 = "YES";
        else str1 = "NO";

        ticketIssueDetail = findViewById(R.id.ticketIssue_detail);
        ticketActionDetail = findViewById(R.id.ticketAction_detail);
        ticketIssueDetail.setText(ticketIssue);
        ticketActionDetail.setText(ticketAction);

        Button previousPageButton= findViewById(R.id.previousPage_button);
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketReportPage2.this, TicketReport.class);
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL",ticketURL);
                goTo.putExtra("ticketReport",ticketReport);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketDateTime",ticketDateTime);
                goTo.putExtra("ticketReportBy", ticketReportBy);
                goTo.putExtra("ticketEscalation", str1);
                goTo.putExtra("ticketEscalationTo", ticketEscalatedTo);
                goTo.putExtra("ticketPIC", ticketPIC);
                goTo.putExtra("ticketIssue", ticketIssue);
                goTo.putExtra("ticketAction", ticketAction);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });

        Button closeTicketButton= findViewById(R.id.closeTicket_button);
        closeTicketButton.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("closedTicket");

            HashMap<String, Object> closedTicketDetails = new HashMap<>();
            closedTicketDetails.put("ticketId", ticketId);
            closedTicketDetails.put("createdtime", ticketDateTime);
            closedTicketDetails.put("severity",ticketPriority);
            closedTicketDetails.put("reportManager", ticketReport);
            closedTicketDetails.put("assignedTo", ticketPIC);
            closedTicketDetails.put("reportedBy", ticketReportBy);
            closedTicketDetails.put("url", ticketURL);
            //closedTicketDetails.put("escalation", ticketEscalation);
            closedTicketDetails.put("escalatedTo", ticketEscalatedTo);
            closedTicketDetails.put("issue", ticketIssue);
            closedTicketDetails.put("action", ticketAction);

            //update the ticket list for filter
            updateReference = database.getReference("ticket");

            HashMap<String, Object> updateTicketDetails = new HashMap<>();
            updateTicketDetails.put("ticketid", ticketId);
            updateTicketDetails.put("ticketissue", ticketIssue);
            updateTicketDetails.put("ticketpriority",ticketPriority);
            updateTicketDetails.put("ticketreport", ticketReport);
            updateTicketDetails.put("ticketurl", ticketURL);
            updateTicketDetails.put("timestamp", ticketDateTime);
            // Updated data
            updateTicketDetails.put("closedStatus", "YES");

            reference.child(ticketId).setValue(closedTicketDetails).addOnSuccessListener(aVoid -> {
                // Data successfully updated on ticket table
                updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateReference.child(ticketId).setValue(updateTicketDetails);}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle onCancelled
                    }
                });

                // Data successfully saved
                Toast.makeText(TicketReportPage2.this, "Closed ticket data saved successfully", Toast.LENGTH_SHORT).show();

                // Here you can navigate to the desired activity after closing the ticket
                Intent goTo = new Intent(TicketReportPage2.this, TicketActivity.class);
                // Pass all UNUSED data back to TicketActivity
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL", ticketURL);
                goTo.putExtra("ticketReport", ticketReport);
                goTo.putExtra("ticketPriority", ticketPriority);
                goTo.putExtra("ticketDateTime", ticketDateTime);
                goTo.putExtra("ticketReportBy", ticketReportBy);
                //goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketEscalationTo", ticketEscalatedTo);
                goTo.putExtra("ticketPIC", ticketPIC);
                goTo.putExtra("ticketIssue", ticketIssue);
                goTo.putExtra("ticketAction", ticketAction);
                goTo.putExtra("username", username);
                goTo.putExtra("userRole", userRole);

                startActivity(goTo);
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save data
                            Toast.makeText(TicketReportPage2.this, "Failed to save closed ticket data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}