package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TicketEscalatedDetailActivity extends AppCompatActivity {
    TextView ticketIdDetail, ticketDateTimeDetail, ticketURLDetail, ticketIssueDetail,
            ticketReportDetail, ticketPriorityDetail;
    Intent goTo;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalated_detail);

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

        Button nextPageButton = findViewById(R.id.nextPage_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo = new Intent(TicketEscalatedDetailActivity.this, TicketEscalatedDetailPage2.class);
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
            }
        });
    }
}