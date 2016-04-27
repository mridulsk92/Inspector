package com.example.mridul_xpetize.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class DashboardActivity extends AppCompatActivity {

    ImageButton myTasks, workers, notification, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Inspector");

        //Initialise
        myTasks = (ImageButton)findViewById(R.id.imageButton_myTasks);
        workers = (ImageButton)findViewById(R.id.imageButton_workers);
        notification = (ImageButton)findViewById(R.id.imageButton_notification);
        logout = (ImageButton)findViewById(R.id.imageButton_logout);

        //onClick of myTasks
        myTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //onClick of workers
        workers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, WorkerListActivity.class);
                startActivity(i);
            }
        });

        //onClick of Notification
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });

        //onClick of Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //trigger dialogbox
                dialogBox();
            }
        });
    }

    //Alert Dialog
    public void dialogBox() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do You Want to Log Out ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Exit
                        System.exit(0);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
