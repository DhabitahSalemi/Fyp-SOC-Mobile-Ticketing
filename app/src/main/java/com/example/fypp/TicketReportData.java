package com.example.fypp;

import java.io.Serializable;

public class TicketReportData implements Serializable
{
    private String actionPlan;

    // Default constructor required for calls to DataSnapshot.getValue(TicketData.class)
    public TicketReportData() {
    }

    public TicketReportData(String actionPlan){
        this.actionPlan = actionPlan;
    }

    public String getActionPlan() {
        return actionPlan;
    }

    public void setActionPlan(String actionPlan) {
        this.actionPlan = actionPlan;
    }
}
