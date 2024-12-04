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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketEscalationApproveForm extends AppCompatActivity {
    EditText approveComments;
    String userRole, username;
    AutoCompleteTextView userList;
    ArrayAdapter<String> adapterItems;
    List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalation_approve_form);

        items = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }

        String ticketId = intent.getStringExtra("ticketId");
        String ticketPriority = intent.getStringExtra("ticketPriority");

        //letak ablik
        String ticketEscalation = intent.getStringExtra("ticketEscalation");
        String ticketReasons = intent.getStringExtra("ticketReasons");

        String ticketEscalatedTo = intent.getStringExtra("ticketEscalatedTo");
        String ticketApproveComments = intent.getStringExtra("ticketApproveComments");
        String ticketDenyComments = intent.getStringExtra("ticketDenyComments");

        userList = findViewById(R.id.assignPIC_list);
        userList.setText(ticketEscalatedTo);
        // Fetch user names from the database
        fetchUserNamesFromDatabase();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Selected item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        approveComments = findViewById(R.id.approveComments_detail);
        approveComments.setText(ticketApproveComments);
        // Set the text of reasons EditText based on the Intent data
        String approveCommentsText = intent.getStringExtra("approveComments");
        if (approveCommentsText != null) {
            approveComments.setText(approveCommentsText);
        }

        Button assignButton = findViewById(R.id.AssignPIC_button);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String escalatedToText = userList.getText().toString();
                String approveCommentsText = approveComments.getText().toString();

                // Ensure a user is selected
                if (escalatedToText.isEmpty()) {
                    Toast.makeText(TicketEscalationApproveForm.this, "Please select a user", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Initialize Firebase Database reference to the existing table
                DatabaseReference escalationRef = FirebaseDatabase.getInstance().getReference().child("escalation");

                HashMap<String, Object> escalationDetails = new HashMap<>();
                escalationDetails.put("requested by", ticketEscalation);
                escalationDetails.put("reasons", ticketReasons);
                escalationDetails.put("escalatedTo", escalatedToText);
                escalationDetails.put("approveComments", approveCommentsText);

                // Push data to the database
                escalationRef.child(ticketId).setValue(escalationDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully saved
                                Toast.makeText(TicketEscalationApproveForm.this, "Escalation data saved successfully", Toast.LENGTH_SHORT).show();

                                Intent goTo = new Intent(TicketEscalationApproveForm.this, TicketActivity.class);
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
                                Toast.makeText(TicketEscalationApproveForm.this, "Failed to save escalation data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                adapterItems = new ArrayAdapter<>(TicketEscalationApproveForm.this, R.layout.option_list, userNames);

                // Set the adapter to the AutoCompleteTextView
                userList.setAdapter(adapterItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TicketEscalationApproveForm.this, "Failed to fetch user names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}