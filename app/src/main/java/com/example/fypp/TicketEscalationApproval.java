package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TicketEscalationApproval extends AppCompatActivity {
    TextView ticketIdDetail, ticketPriorityDetail,ticketRequestByDetail, ticketReasonsDetail;
    String userRole, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalation_approval);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");}

        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");}

        String ticketId = intent.getStringExtra("ticketId");
        String ticketPriority = intent.getStringExtra("ticketPriority");
        String ticketEscalation = intent.getStringExtra("ticketEscalation");
        String ticketReasons = intent.getStringExtra("ticketReasons");
        String ticketEscalatedTo = intent.getStringExtra("ticketEscalatedTo");
        String ticketApproveComments = intent.getStringExtra("ticketApproveComments");
        String ticketDenyComments = intent.getStringExtra("ticketDenyComments");

        ticketIdDetail = findViewById(R.id.ticketid_detail);
        ticketPriorityDetail = findViewById(R.id.ticketPriority_detail);
        ticketRequestByDetail = findViewById(R.id.requestedby_detail);
        ticketReasonsDetail = findViewById(R.id.reason_detail);
        ticketIdDetail.setText(ticketId);
        ticketPriorityDetail.setText(ticketPriority);
        ticketRequestByDetail.setText(ticketEscalation);
        ticketReasonsDetail.setText(ticketReasons);

        Button approveButton = findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketEscalationApproval.this, TicketEscalationApproveForm.class);
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketReasons",ticketReasons);
                goTo.putExtra("ticketEscalatedTo",ticketEscalatedTo);
                goTo.putExtra("ticketApproveComments",ticketApproveComments);
                goTo.putExtra("ticketDenyComments",ticketDenyComments);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });

        Button denyButton = findViewById(R.id.deny_button);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTo = new Intent(TicketEscalationApproval.this, TicketEscalationDenyForm.class);
                goTo.putExtra("ticketId", ticketId);
                goTo.putExtra("ticketPriority",ticketPriority);
                goTo.putExtra("ticketEscalation", ticketEscalation);
                goTo.putExtra("ticketReasons",ticketReasons);
                goTo.putExtra("ticketEscalatedTo",ticketEscalatedTo);
                goTo.putExtra("ticketApproveComments",ticketApproveComments);
                goTo.putExtra("ticketDenyComments",ticketDenyComments);
                goTo.putExtra("userRole",userRole);
                goTo.putExtra("username", username);
                startActivity(goTo);
            }
        });
    }
}