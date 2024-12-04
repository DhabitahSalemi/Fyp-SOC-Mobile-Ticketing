package com.example.fypp;

import java.io.Serializable;

public class TicketData implements Serializable {
    private String ticketid;
    private String ticketurl;
    private String ticketissue;
    private String ticketreport;
    private String ticketpriority;
    private Object timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(TicketData.class)
    public TicketData() {
    }

    public TicketData(String ticketid, String ticketurl, String ticketissue, String ticketreport, String ticketpriority, Object timestamp) {
        this.ticketid = ticketid;
        this.ticketurl = ticketurl;
        this.ticketissue = ticketissue;
        this.ticketreport = ticketreport;
        this.ticketpriority = ticketpriority;
        this.timestamp = timestamp;
    }

    // Getters and setters for all fields
    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getTicketurl() {
        return ticketurl;
    }

    public void setTicketurl(String ticketurl) {
        this.ticketurl = ticketurl;
    }

    public String getTicketissue() {
        return ticketissue;
    }

    public void setTicketissue(String ticketissue) {
        this.ticketissue = ticketissue;
    }

    public String getTicketreport() {
        return ticketreport;
    }

    public void setTicketreport(String ticketreport) {
        this.ticketreport = ticketreport;
    }

    public String getTicketpriority() {
        return ticketpriority;
    }

    public void setTicketpriority(String ticketpriority) {
        this.ticketpriority = ticketpriority;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
