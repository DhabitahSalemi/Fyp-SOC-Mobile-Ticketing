package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TicketReport extends AppCompatActivity {
    TextView ticketIdDetail, ticketDateTimeDetail, ticketPriorityDetail, ticketEscalatedDetail, ticketReportByDetail,
            ticketReportDetail, ticketPicDetail, ticketURLDetail;

    String userRole, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_report);

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
        String ticketDateTime = intent.getStringExtra("ticketDateTime");
        String ticketPriority = intent.getStringExtra("ticketPriority");
        String ticketReport = intent.getStringExtra("ticketReport");
        String ticketPIC = intent.getStringExtra("ticketPIC");
        String ticketURL = intent.getStringExtra("ticketURL");
        String ticketEscalation = intent.getStringExtra("ticketEscalation");
        String ticketEscalatedTo = intent.getStringExtra("ticketEscalatedTo");
        String ticketReportBy = intent.getStringExtra("ticketReportBy");

        String ticketAction = intent.getStringExtra("ticketAction");
        String ticketIssue = intent.getStringExtra("ticketIssue");

        String str1, str2, str3;

        if (ticketEscalation != null) str1 = "YES";
        else str1 = "NO";

        if (ticketReportBy != null) str2 = ticketReportBy;
        else str2 = "NO ONE";

        if (ticketAction != null) str3 = ticketAction;
        else str3 = "NO ACTION TAKEN YET";

        ticketIdDetail = findViewById(R.id.ticketid_detail);
        ticketDateTimeDetail = findViewById(R.id.ticketDateTime_detail);
        ticketPriorityDetail = findViewById(R.id.ticketPriority_detail);
        ticketReportDetail = findViewById(R.id.ticketReport_detail);
        ticketPicDetail = findViewById(R.id.assignTo_detail);
        ticketURLDetail = findViewById(R.id.ticketURL_detail);
        ticketEscalatedDetail = findViewById(R.id.ticketEscalated_detail);
        ticketReportByDetail = findViewById(R.id.reportedBy_detail);

        ticketIdDetail.setText(ticketId);
        ticketDateTimeDetail.setText(ticketDateTime);
        ticketPriorityDetail.setText(ticketPriority);
        ticketReportDetail.setText(ticketReport);
        ticketPicDetail.setText(ticketPIC);
        ticketURLDetail.setText(ticketURL);
        ticketEscalatedDetail.setText(str1);
        ticketReportByDetail.setText(str2);

        Button nextPageButton= findViewById(R.id.nextPage_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketReport.this, TicketReportPage2.class);
                // Pass all UNUSED data to TicketReport2
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketURL",ticketURL);
                goTo.putExtra("ticketReport",ticketReport);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketDateTime",ticketDateTime);

                goTo.putExtra("ticketReportBy", ticketReportBy);
                goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketEscalationTo", ticketEscalatedTo);
                goTo.putExtra("ticketPIC", ticketPIC);

                // Pass ticketIssue and ticketAction data to TicketReport2
                goTo.putExtra("ticketIssue",ticketIssue);
                goTo.putExtra("ticketAction", str3);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });
    }
}