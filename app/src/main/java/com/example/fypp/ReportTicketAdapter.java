package com.example.fypp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportTicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ENTRY = 0;
    Context context;
    ArrayList<TicketData> list;
    ArrayList<TicketAssignData> picList; // Add ArrayList for TicketAssignData
    String userRole, username;
    Intent goTo;
    public ReportTicketAdapter(Context context, ArrayList<TicketData> list, ArrayList<TicketAssignData> picList,String userRole,String username) {
        this.context = context;
        this.list = list;
        this.picList = picList;
        this.userRole = userRole; // Store the user role!
        this.username = username; // Store the user name!
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_ENTRY:
                v = LayoutInflater.from(context).inflate(R.layout.ticket_entry, parent, false);
                return new TicketEntryViewHolder(v);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        TicketData ticket = list.get(position);

        TicketEntryViewHolder entryHolder = (TicketEntryViewHolder) holder;

        entryHolder.id.setText(ticket.getTicketid());
        entryHolder.priority.setText(ticket.getTicketpriority());
        entryHolder.description.setText(ticket.getTicketissue());

        entryHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click for ticket entry
                if (userRole != null && userRole.equals("Manager")) {
                    goTo = new Intent(context, TicketReport.class);
                } else {
                    goTo = new Intent(context, TicketDetailActivity.class);
                }

                // Get reference to "assign"
                DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference().child("assign").child(ticket.getTicketid());
                // Get reference to "escalation"
                DatabaseReference escalateRef = FirebaseDatabase.getInstance().getReference().child("escalation").child(ticket.getTicketid());
                // Get reference to "report"
                DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference().child("report").child(ticket.getTicketid());

                // Listen for changes on the "assign" reference
                assignRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If data exists in "assign", get ticketPIC and put it in the intent
                            String ticketPIC = dataSnapshot.child("ticketPIC").getValue(String.class);
                            goTo.putExtra("ticketPIC", ticketPIC);
                        } else {
                            // If data doesn't exist in "assign", put a default value in the intent
                            String ticketPIC = "null";
                            goTo.putExtra("ticketPIC", ticketPIC);
                        }

                        escalateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String ticketEscalation = dataSnapshot.child("requested by").getValue(String.class);
                                String ticketReasons = dataSnapshot.child("reasons").getValue(String.class);
                                String ticketApproveComments = dataSnapshot.child("ticketApproveComments").getValue(String.class);
                                goTo.putExtra("ticketEscalation", ticketEscalation);
                                goTo.putExtra("ticketReasons", ticketReasons);
                                goTo.putExtra("ticketApproveComments", ticketApproveComments);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle onCancelled
                            }
                        });

                        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String ticketReportBy = dataSnapshot.child("reported by").getValue(String.class);
                                String ticketAction = dataSnapshot.child("action plan").getValue(String.class);
                                goTo.putExtra("ticketReportBy", ticketReportBy);
                                goTo.putExtra("ticketAction", ticketAction);

                                goTo.putExtra("ticketId", ticket.getTicketid());
                                goTo.putExtra("ticketURL",ticket.getTicketurl());
                                goTo.putExtra("ticketIssue",ticket.getTicketissue());
                                goTo.putExtra("ticketReport",ticket.getTicketreport());
                                goTo.putExtra("ticketPriority",ticket.getTicketpriority());
                                goTo.putExtra("ticketDateTime",ticket.getTimestamp().toString());

                                goTo.putExtra("userRole",userRole);
                                goTo.putExtra("username",username);

                                // Start the activity after putting extra data from both references
                                context.startActivity(goTo);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle onCancelled
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle onCancelled
                    }
                }); //ref
            }//onclick
        }); //onclick listener

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TicketEntryViewHolder extends RecyclerView.ViewHolder {
        TextView id, priority, description;
        public TicketEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.ticketid_text);
            priority = itemView.findViewById(R.id.ticketpriority_text);
            description = itemView.findViewById(R.id.ticketdesc_text);
        }
    }

}