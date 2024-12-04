package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TicketEscalationDenyForm extends AppCompatActivity {
    EditText denyComments;
    String userRole, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalation_deny_form);

        Intent intent = getIntent();
        String ticketId = intent.getStringExtra("ticketId");
        String ticketPriority = intent.getStringExtra("ticketPriority");
        String ticketEscalation = intent.getStringExtra("ticketEscalation");
        String ticketReasons = intent.getStringExtra("ticketReasons");
        String ticketEscalatedTo = intent.getStringExtra("ticketEscalatedTo");
        String ticketApproveComments = intent.getStringExtra("ticketApproveComments");
        String ticketDenyComments = intent.getStringExtra("ticketDenyComments");

        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        denyComments = findViewById(R.id.denyComments_detail);
        denyComments.setText(ticketApproveComments);
        // Set the text of reasons EditText based on the Intent data
        String denyCommentsText = intent.getStringExtra("denyComments");
        if (denyCommentsText != null) {
            denyComments.setText(denyCommentsText);
        }



        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String denyCommentsText = denyComments.getText().toString();

                // Initialize Firebase Database reference to the existing table (e.g., escalation)
                DatabaseReference escalationRef = FirebaseDatabase.getInstance().getReference().child("escalation");

                HashMap<String, Object> escalationDetails = new HashMap<>();
                escalationDetails.put("requested by", ticketEscalation);
                escalationDetails.put("reasons", ticketReasons);

                escalationDetails.put("denyComments", denyCommentsText);

                escalationRef.child(ticketId).setValue(escalationDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully saved
                                Toast.makeText(TicketEscalationDenyForm.this, "Escalation data saved successfully", Toast.LENGTH_SHORT).show();

                                Intent goTo = new Intent(TicketEscalationDenyForm.this, TicketActivity.class);
                                goTo.putExtra("ticketId", ticketId);
                                goTo.putExtra("ticketPriority",ticketPriority);
                                goTo.putExtra("ticketEscalation",ticketEscalation);
                                goTo.putExtra("ticketReasons",ticketReasons);
                                goTo.putExtra("ticketEscalatedTo",ticketEscalatedTo);
                                goTo.putExtra("ticketApproveComments",ticketApproveComments);

                                goTo.putExtra("ticketDenyComments",ticketDenyComments);
                                goTo.putExtra("userRole",userRole);
                                goTo.putExtra("username", username);
                                startActivity(goTo);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to save data
                                Toast.makeText(TicketEscalationDenyForm.this, "Failed to save escalation data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
}