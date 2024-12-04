package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TicketEscalatedDetailPage2 extends AppCompatActivity {
    TextView  previousPICDetail, managerCommentsDetail;
    Intent goTo;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalated_detail_page2);

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

        String ticketReasons = intent.getStringExtra("ticketReasons");
        String ticketPreviousPIC = intent.getStringExtra("ticketPreviousPIC");
        String ticketApproveComments = intent.getStringExtra("ticketApproveComments");

        previousPICDetail = findViewById(R.id.previousPIC_detail);
        managerCommentsDetail = findViewById(R.id.managerComments_detail);

        previousPICDetail.setText(ticketPreviousPIC);
        managerCommentsDetail.setText(ticketApproveComments);

        Button reportButton = findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(TicketEscalatedDetailPage2.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.analyst_report_workflow_dialog);

                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                Button continueButton = dialog.findViewById(R.id.continue_button);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goTo = new Intent(TicketEscalatedDetailPage2.this, TicketReportForm.class);
                        goTo.putExtra("ticketId", ticketId);
                        goTo.putExtra("ticketURL", ticketURL);
                        goTo.putExtra("ticketIssue", ticketIssue);
                        goTo.putExtra("ticketReport",ticketReport);
                        goTo.putExtra("ticketPriority", ticketPriority);
                        goTo.putExtra("ticketDateTime", ticketDateTime);
                        goTo.putExtra("ticketPreviousPIC", ticketPreviousPIC);
                        goTo.putExtra("ticketApproveComments", ticketApproveComments);
                        goTo.putExtra("ticketReasons", ticketReasons);
                        goTo.putExtra("username", username);
                        goTo.putExtra("userRole", userRole);
                        startActivity(goTo);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        Button previousPageButton= findViewById(R.id.previousPage_button);
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo = new Intent(TicketEscalatedDetailPage2.this,TicketEscalatedDetailActivity.class);
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL", ticketURL);
                goTo.putExtra("ticketIssue", ticketIssue);
                goTo.putExtra("ticketReport",ticketReport);
                goTo.putExtra("ticketPriority", ticketPriority);
                goTo.putExtra("ticketDateTime", ticketDateTime);
                goTo.putExtra("ticketPreviousPIC", ticketPreviousPIC);
                goTo.putExtra("ticketApproveComments", ticketApproveComments);
                goTo.putExtra("username", username);
                goTo.putExtra("userRole", userRole);
                startActivity(goTo);
            }
        });
    }
}