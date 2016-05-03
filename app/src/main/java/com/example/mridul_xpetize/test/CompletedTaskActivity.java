package com.example.mridul_xpetize.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CompletedTaskActivity extends AppCompatActivity {

    ImageView img;
    Button approve, reject;
    ProgressDialog pDialog;
    String username,task_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);

        //Initialise
        img = (ImageView) findViewById(R.id.imageView2);
        approve = (Button)findViewById(R.id.button_approve);
        reject = (Button)findViewById(R.id.button_reject);

        //getIntent
        Intent i = getIntent();
        username = i.getStringExtra("name");
        task_id = i.getStringExtra("id");

        //onClick of approve button
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ApproveTask().execute();
            }
        });

        //onClick of reject button
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RejectTask().execute();
            }
        });
    }

    //AsyncTask to approve tasks(to be edited)
    private class ApproveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CompletedTaskActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_ApproveTsk&Para=" + task_id;
            Log.d("url",url);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Intent i = new Intent(CompletedTaskActivity.this, NotificationActivity.class);
            startActivity(i);
        }
    }

    //AsyncTask to reject tasks(to be edited)
    private class RejectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CompletedTaskActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_RejectTsk&Para=" + task_id;
            Log.d("url",url);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Intent i = new Intent(CompletedTaskActivity.this, NotificationActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }
}
