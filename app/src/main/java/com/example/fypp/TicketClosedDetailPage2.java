package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketClosedDetailPage2 extends AppCompatActivity {
    TextView ticketIssueDetail,ticketActionDetail;
    String userRole, username;
    FirebaseDatabase database;
    DatabaseReference reference, updateReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_closed_detail_page2);

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

        // Retrieve data from the Intent USED HERE
        String ticketAction = intent.getStringExtra("ticketAction");
        String ticketIssue = intent.getStringExtra("ticketIssue");

        ticketIssueDetail = findViewById(R.id.ticketIssue_detail);
        ticketActionDetail = findViewById(R.id.ticketAction_detail);

        ticketIssueDetail.setText(ticketIssue);
        ticketActionDetail.setText(ticketAction);

        Button previousPageButton= findViewById(R.id.previousPage_button);
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketClosedDetailPage2.this, TicketReport.class);
                // Pass all UNUSED data back to TicketReport
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL",ticketURL);
                goTo.putExtra("ticketReport",ticketReport);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketDateTime",ticketDateTime);

                goTo.putExtra("ticketReportBy", ticketReportBy);
                goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketPIC", ticketPIC);

                // Pass ticketIssue and ticketAction data back to TicketReport
                goTo.putExtra("ticketIssue", ticketIssue);
                goTo.putExtra("ticketAction", ticketAction);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });

        Button closeButton= findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketClosedDetailPage2.this,TicketActivity.class);
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL",ticketURL);
                goTo.putExtra("ticketReport",ticketReport);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketDateTime",ticketDateTime);

                goTo.putExtra("ticketReportBy", ticketReportBy);
                goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketPIC", ticketPIC);

                // Pass ticketIssue and ticketAction data back to TicketReport
                goTo.putExtra("ticketIssue", ticketIssue);
                goTo.putExtra("ticketAction", ticketAction);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });
    }
}