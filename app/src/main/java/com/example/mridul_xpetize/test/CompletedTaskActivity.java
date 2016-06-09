package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    String priority;
    CheckBox high, medium, low;

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

                LayoutInflater factory = LayoutInflater.from(CompletedTaskActivity.this);
                final View addView = factory.inflate(
                        R.layout.comment_dialog, null);
                final AlertDialog addDialog = new AlertDialog.Builder(CompletedTaskActivity.this).create();
                addDialog.setView(addView);

                //Initialise
                final EditText comment = (EditText)addView.findViewById(R.id.editText_comment);
                high = (CheckBox)addView.findViewById(R.id.checkBox_high);
                medium = (CheckBox)addView.findViewById(R.id.checkBox_medium);
                low = (CheckBox)addView.findViewById(R.id.checkBox_low);
                Button reject = (Button)addView.findViewById(R.id.button_reject);

                high.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(high.isChecked()){
                            priority = "High";
                            medium.setChecked(false);
                            low.setChecked(false);
                        }
                    }
                });

                medium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(medium.isChecked()){
                            priority = "Medium";
                            high.setChecked(false);
                            low.setChecked(false);
                        }
                    }
                });

                low.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(low.isChecked()){
                            priority = "Low";
                            high.setChecked(false);
                            medium.setChecked(false);
                        }
                    }
                });

                //Reject Button onClick
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment_st = comment.getText().toString();
//                        new RejectTask().execute();
                    }
                });
                addDialog.show();
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

            String url = getString(R.string.url)+"MyService.asmx/ExcProcedure?Para=Proc_ApproveTsk&Para=" + task_id;
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

            String url = getString(R.string.url_string)+"MyService.asmx/ExcProcedure?Para=Proc_RejectTsk&Para=" + task_id;
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
