package com.example.fypp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.List;

public class TicketAssignForm extends AppCompatActivity {
    TextView ticketIdDetail, ticketDateTimeDetail, ticketURLDetail, ticketIssueDetail,
            ticketReportDetail, ticketPriorityDetail;

    String userRole,username;

    FirebaseDatabase database;
    DatabaseReference reference;

    AutoCompleteTextView userList;
    ArrayAdapter<String> adapterItems;
    List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_assign_form);

        // Declare DatabaseReference
        DatabaseReference assignReference;

        // Inside onCreate() or wherever appropriate in your activity
        assignReference = FirebaseDatabase.getInstance().getReference("PIC");

        // Initialize items list
        items = new ArrayList<>();

        //get extra bundle
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

        // Display the data
        ticketIdDetail.setText(ticketId);
        ticketURLDetail.setText(ticketURL);
        ticketIssueDetail.setText(ticketIssue);
        ticketReportDetail.setText(ticketReport);
        ticketPriorityDetail.setText(ticketPriority);
        ticketDateTimeDetail.setText(ticketDateTime);

        userList = findViewById(R.id.assignPIC_list);

        // Fetch user names from the database
        fetchUserNamesFromDatabase();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), "Selected item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // Assign Button
        Button assignedButton = findViewById(R.id.AssignPIC_button);

        assignedButton.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("assign");
            String ticketId1 = ticketIdDetail.getText().toString(); // Retrieve ticket ID
            String pic = userList.getText().toString();
            TicketAssignData ticketAssignData = new TicketAssignData(pic);

            reference.child(ticketId1).setValue(ticketAssignData)
                    .addOnCompleteListener(task -> {
                        // Display success dialog
                        Dialog dialog = new Dialog(TicketAssignForm.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.success_assigned_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
                        // button in dialog layout
                        Button successOK = dialog.findViewById(R.id.ok_btn);
                        successOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {
                                // Navigate back to TicketActivity
                                Intent goTo = new Intent(TicketAssignForm.this, TicketActivity.class);
                                goTo.putExtra("username",username);
                                goTo.putExtra("userRole",userRole);
                                startActivity(goTo);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();// Display the dialog
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
                adapterItems = new ArrayAdapter<>(TicketAssignForm.this, R.layout.option_list, userNames);
                // Set the adapter to the AutoCompleteTextView
                userList.setAdapter(adapterItems);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TicketAssignForm.this, "Failed to fetch user names: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}