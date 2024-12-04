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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketForm extends AppCompatActivity {
    TextView ticketID;
    EditText ticketIssueIdentified;
    Button createTicketFormButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    String[] url = {"wifi-student.msu.edu.my","eklas.msu.edu.my","www.msucollege.edu.my",
            "db3.msumedicalcentre.com","app1.msumedicalcentre.com","app2.msumedicalcentre.com",
            "klas2.jgu.ac.id","kpi.msu.edu.my","kpi.msumedicalcentre.com","kpi.jgu.ac.id",
            "speedtest.msu.edu.my","ldap.msu.edu.my","alumni.msu.edu.my","job-app.msu.edu.my",
            "ojs.msu.edu.my","srm.msu.edu.my","survey.msu.edu.my","isrm.msu.edu.my","www.jgu.ac.id",
            "ai.msu.edu.my","klas2.msu.edu.my","portal.msu.edu.my","srm.jgu.ac.id","hrm.jgu.ac.id",
            "fms.msu.edu.my","klas2t.msu.edu.my","cgp-klas2.msu.edu.my","pos.msu.edu.my",
            "his.msu.edu.my","demo-klas2.msucollege.edu.my","klas2.msucollege.edu.my",
            "klas2.klsentral.msucollege.edu.my","klas2msu.klsentral.msucollege.edu.my",
            "klas2.seremban.msucollege.edu.my","klas2msu.seremban.msucollege.edu.my",
            "klas2.sp.msucollege.edu.my","klas2msu.sp.msucollege.edu.my","klas2.kb.msucollege.edu.my",
            "klas2msu.kb.msucollege.edu.my","klas2.sa.msucollege.edu.my","klas2.kajang.msucollege.edu.my",
            "demo-msuc.msu.edu.my"};
    String[] report = {"YES","NO"};
    String[] priority = {"1","2","3","4","5"};

    AutoCompleteTextView ticketUrlList,ticketReportList,ticketPriorityList;
    ArrayAdapter<String> adapterItems;
    String userRole, username;

    int ticketNumber; // Initialize the ticket number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_form);

        // Declare DatabaseReference
        DatabaseReference ticketReference;

        // Inside onCreate() or wherever appropriate in your activity
        ticketReference = FirebaseDatabase.getInstance().getReference("ticket");

        // Retrieve the count of tickets and increment the ticket number
        ticketReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int ticketCount = (int) snapshot.getChildrenCount();
                    ticketNumber = ticketCount + 1;
                } else {
                    // Handle the case when there are no tickets in the database
                    ticketNumber = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });

        //ticketID = findViewById(R.id.ticket_ID);
        //ticketID = findViewById(R.id.ticket_ID_display); // Reference to the TextView for displaying ticket ID
        ticketUrlList = findViewById(R.id.ticket_url);
        ticketIssueIdentified = findViewById(R.id.ticket_issueIdentified);
        createTicketFormButton = findViewById(R.id.ticket_form_button);
        ticketReportList = findViewById(R.id.ticket_report_list);

        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, url);
        ticketUrlList.setAdapter(adapterItems);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("userRole")) {
            userRole = intent.getStringExtra("userRole");
            // Now you have the userRole, you can use it as needed
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            // Now you have the username, you can use it as needed
        }


        ticketUrlList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedReport = (String) parent.getItemAtPosition(position);
                // Handle the selected report option here
            }
        });

        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, report);
        ticketReportList.setAdapter(adapterItems);

        ticketReportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedReport = (String) parent.getItemAtPosition(position);
                // Handle the selected report option here
            }
        });

        ticketPriorityList = findViewById(R.id.ticket_priority_list);
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priority);
        ticketPriorityList.setAdapter(adapterItems);

        ticketPriorityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPriority = (String) parent.getItemAtPosition(position);
                // Handle the selected option here
            }
        });
        createTicketFormButton.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("ticket");

            String id = generateTicketID();
            String url = ticketUrlList.getText().toString();
            String issue = ticketIssueIdentified.getText().toString();
            String report = ticketReportList.getText().toString();
            String priority = ticketPriorityList.getText().toString();

            // Get current timestamp in milliseconds
            long timestamp = System.currentTimeMillis();
            // Convert timestamp to a human-readable date and time format
            String formattedDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(timestamp));

            TicketData ticketData = new TicketData(id,url,issue, report, priority, formattedDateTime);

            // Save the ticket data to the database
            reference.child(id).setValue(ticketData).addOnCompleteListener(task -> {
                        //Create a new dialog
                        Dialog dialog = new Dialog(TicketForm.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.create_ticket_success_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                        Button successOK = dialog.findViewById(R.id.ok_btn);
                        successOK.setOnClickListener(view1 -> {
                            Intent ticketPage = new Intent(TicketForm.this, TicketAnalystActivity.class);
                            ticketPage.putExtra("ticketId",id);
                            ticketPage.putExtra("ticketURL",url);
                            ticketPage.putExtra("ticketIssue",issue);
                            ticketPage.putExtra("ticketReport",report);
                            ticketPage.putExtra("ticketPriority",priority);
                            ticketPage.putExtra("ticketDateTime",formattedDateTime);
                            ticketPage.putExtra("userRole",userRole);
                            ticketPage.putExtra("username", username);
                            startActivity(ticketPage);
                            dialog.dismiss();
                        });
                        dialog.show(); // Display the dialog
                    });
        });
    }

    // Method to generate the ticket ID
    private String generateTicketID() {
        String ticketNumberString = String.format("%04d", ticketNumber);
        return "CSAICT" + ticketNumberString;
    }
}