package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicketDetailActivity extends AppCompatActivity {
    TextView ticketIdDetail, ticketDateTimeDetail, ticketURLDetail, ticketIssueDetail,
            ticketReportDetail, ticketPriorityDetail;

    ArrayList<TicketData> list;
    Intent goTo;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

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
        String ticketURL = intent.getStringExtra("ticketURL");
        String ticketIssue = intent.getStringExtra("ticketIssue");
        String ticketReport = intent.getStringExtra("ticketReport");
        String ticketPriority = intent.getStringExtra("ticketPriority");
        String ticketDateTime = intent.getStringExtra("ticketDateTime");

        ticketIdDetail = findViewById(R.id.ticketid_detail);
        ticketDateTimeDetail = findViewById(R.id.ticketDateTime_detail);
        ticketURLDetail = findViewById(R.id.ticketURL_detail);
        ticketIssueDetail = findViewById(R.id.ticketIssue_detail);
        ticketReportDetail = findViewById(R.id.ticketReport_detail);
        ticketPriorityDetail = findViewById(R.id.ticketPriority_detail);

        ticketIdDetail.setText(ticketId);
        ticketURLDetail.setText(ticketURL);
        ticketIssueDetail.setText(ticketIssue);
        ticketReportDetail.setText(ticketReport);
        ticketPriorityDetail.setText(ticketPriority);
        ticketDateTimeDetail.setText(ticketDateTime);

        Button reportButton = findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(TicketDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.analyst_report_workflow_dialog);

                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                Button continueButton = dialog.findViewById(R.id.continue_button);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goTo = new Intent(TicketDetailActivity.this, TicketReportForm.class);
                        goTo.putExtra("ticketId", ticketId);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        Button reqEscalateButton = findViewById(R.id.req_escalate_button);
        //add db ref if the id have escalate or not
        // Get reference to "escalation"
        DatabaseReference escalateRef = FirebaseDatabase.getInstance().getReference().child("escalation").child(ticketId);

        boolean escalated = false;

        reqEscalateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                escalateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If data exists
                            // If data doesn't exist
                            // Create a new dialog
                            Dialog dialog = new Dialog(TicketDetailActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.escalate_ticket_workflow_dialog2);

                            // Set the dialog window's background to the custom drawable
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                            // button in the dialog layout
                            Button continueButton = dialog.findViewById(R.id.continue_button);
                            continueButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            // Display the dialog
                            dialog.show();

                        } else {
                            // If data doesn't exist
                            // Create a new dialog
                            Dialog dialog = new Dialog(TicketDetailActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.escalate_ticket_workflow_dialog);

                            // Set the dialog window's background to the custom drawable
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                            // button in the dialog layout
                            Button continueButton = dialog.findViewById(R.id.continue_button);
                            continueButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent goTo = new Intent(TicketDetailActivity.this, TicketEscalationForm.class);
                                    goTo.putExtra("ticketId", ticketId);
                                    goTo.putExtra("username", username);
                                    goTo.putExtra("userRole", userRole);
                                    startActivity(goTo);
                                    dialog.dismiss();
                                }
                            });
                            // Display the dialog
                            dialog.show();
                        }//else
                    }//on change

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle onCancelled
                    }
                }); //reference


            } // onCLick
        });
    }

}
