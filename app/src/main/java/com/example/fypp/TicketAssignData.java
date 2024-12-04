package com.example.fypp;

import java.io.Serializable;

public class TicketAssignData implements Serializable
{
    private String pic;

    // Default constructor required for calls to DataSnapshot.getValue(TicketData.class)
    public TicketAssignData() {
    }

    public TicketAssignData(String pic){
        this.pic = pic;
    }

    public String getTicketPIC() {
        return pic;
    }

    public void setTicketPIC(String ticketPIC) {
        this.pic = pic;
    }
}
