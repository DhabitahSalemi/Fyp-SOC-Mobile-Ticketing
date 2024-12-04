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

public class TicketEscalationForm extends AppCompatActivity {
    EditText reasons;
    FirebaseDatabase database;
    DatabaseReference reference;
    AutoCompleteTextView userList;
    ArrayAdapter<String> adapterItems;
    List<String> items;
    String userRole,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_escalation_form);

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

        userList = findViewById(R.id.requestedby_list);

        // Fetch user names from the database
        fetchUserNamesFromDatabase();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Selected item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        reasons = findViewById(R.id.reason_detail);
        // Set the text of reasons EditText based on the Intent data
        String reasonsText = intent.getStringExtra("reasons");
        if (reasonsText != null) {
            reasons.setText(reasonsText);
        }


        // submitRequest Button
        Button submitRequestButton = findViewById(R.id.submitRequest_button);

        submitRequestButton.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("escalation");

            String ticketId1 = ticketIdDetail.getText().toString(); // Retrieve ticket ID
            String requestedBy = userList.getText().toString();
            String reasonsText1 = reasons.getText().toString();

            // Ensure a user is selected
            if (requestedBy.isEmpty()) {
                Toast.makeText(TicketEscalationForm.this, "Please select a user", Toast.LENGTH_SHORT).show();
                return;}

            // Create a HashMap to hold both the action plan and the user who reported it
            HashMap<String, Object> escalationDetails = new HashMap<>();
            escalationDetails.put("requested by", requestedBy);
            escalationDetails.put("reasons", reasonsText1);

            // Set the escalation details in the database
            reference.child(ticketId1).setValue(escalationDetails).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent goTo = new Intent(TicketEscalationForm.this, TicketAnalystActivity.class);
                            goTo.putExtra("reasons", reasonsText1);
                            goTo.putExtra("username", username);
                            goTo.putExtra("userRole", userRole);
                            startActivity(goTo);
                        } else {
                            Toast.makeText(TicketEscalationForm.this, "Failed to submit report: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
                adapterItems = new ArrayAdapter<>(TicketEscalationForm.this, R.layout.option_list, userNames);

                // Set the adapter to the AutoCompleteTextView
                userList.setAdapter(adapterItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TicketEscalationForm.this, "Failed to fetch user names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}