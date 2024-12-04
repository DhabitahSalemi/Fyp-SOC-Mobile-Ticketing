package com.example.fypp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNavigationView;

    HomeAnalystActivity homeFragment = new HomeAnalystActivity();
    NotificationActivity notificationFragment = new NotificationActivity();
    MessageActivity messageFragment = new MessageActivity();
    TicketActivity ticketActivityFragment = new TicketActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }
}