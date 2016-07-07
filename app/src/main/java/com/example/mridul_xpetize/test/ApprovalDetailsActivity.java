package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ApprovalDetailsActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    String userId_st;
    PreferencesHelper pref;
    String id, detail_id, assignedBy, createdBy, name_st, desc_st, comments_st, startDate, endDate, status_st, assignedTo_st, comments_updated;
    int response_json;
    Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        //Get Preference Values
        pref = new PreferencesHelper(ApprovalDetailsActivity.this);
        final String acc_name = pref.GetPreferences("UserName");


        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(acc_name).withEmail(acc_name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        //Drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        pref = new PreferencesHelper(ApprovalDetailsActivity.this);
        Button approve = (Button)findViewById(R.id.button_approve);
        Button reject = (Button)findViewById(R.id.button_reject);
        TextView name = (TextView)findViewById(R.id.name);
        TextView desc = (TextView)findViewById(R.id.desc);
        TextView comments = (TextView)findViewById(R.id.comments);
        TextView assignedTo = (TextView)findViewById(R.id.assigned);
        TextView start = (TextView)findViewById(R.id.start);
        TextView end = (TextView)findViewById(R.id.end);
        TextView status = (TextView)findViewById(R.id.status);
//        TextView desc = (TextView)findViewById(R.id.desc);
//        TextView desc = (TextView)findViewById(R.id.desc);
//        TextView desc = (TextView)findViewById(R.id.desc);

        //Get Intent
        Intent i = getIntent();
        id = i.getStringExtra("Id");
        assignedTo_st = i.getStringExtra("AssignedTo");
        detail_id = i.getStringExtra("DetailsId");
        assignedBy = i.getStringExtra("AssignedById");
        createdBy = i.getStringExtra("CreatedById");
        name_st = i.getStringExtra("Name");
        desc_st = i.getStringExtra("Desc");
        comments_st = i.getStringExtra("Comments");
        startDate = i.getStringExtra("StartDate");
        endDate = i.getStringExtra("EndDate");
        status_st = i.getStringExtra("StatusId");

        //Set TextView Values
        name.setText(name_st);
        desc.setText(desc_st);
        comments.setText(comments_st);
        assignedTo.setText("Assigned To : "+assignedBy);
        start.setText(startDate);
        end.setText(endDate);
        status.setText(status_st);

        //onClick of Approve
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitDialog("Approve");
            }
        });

        //onClick of Reject
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitDialog("Reject");
            }
        });
    }

    private void SubmitDialog(String arg0) {

        final String condition = arg0;
        LayoutInflater factory = LayoutInflater.from(ApprovalDetailsActivity.this);
        final View addView = factory.inflate(
                R.layout.submit_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(ApprovalDetailsActivity.this).create();
        addDialog.setView(addView);

        //Initialise
        final EditText commentBox = (EditText) addView.findViewById(R.id.subimt_comment_text);
        Button submitTask = (Button) addView.findViewById(R.id.button_submit);

        //onClick of SubmitButton
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comments_updated = commentBox.getText().toString();
                new PostTask().execute(condition);
            }
        });

        addDialog.show();
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }
    
    private class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // Creating service handler class instance

            String check = arg0[0];
            JSONStringer userJson = null;
            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/UpdateAssignedTask");
            String temp_start = getCurrentTimeStamp();
            String temp_end = getCurrentTimeStamp();

            if(check.equals("Approve")) {

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                // Build JSON string
                try {
                    userJson = new JSONStringer()
                            .object()
                            .key("taskDetails")
                            .object()
                            .key("TaskDetailsId").value(detail_id)
                            .key("TaskId").value(id)
                            .key("AssignedToId").value(assignedTo_st)
                            .key("StartDateStr").value(temp_start)
                            .key("EndDateStr").value(temp_end)
                            .key("AssignedById").value(assignedBy)
                            .key("StatusId").value(7)
                            .key("IsSubTask").value(1)
                            .key("Comments").value(comments_updated)
                            .key("CreatedBy").value(createdBy)
                            .endObject()
                            .endObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                // Build JSON string
                try {
                    userJson = new JSONStringer()
                            .object()
                            .key("taskDetails")
                            .object()
                            .key("TaskDetailsId").value(detail_id)
                            .key("TaskId").value(id)
                            .key("AssignedToId").value(assignedTo_st)
                            .key("StartDateStr").value(temp_start)
                            .key("EndDateStr").value(temp_end)
                            .key("AssignedById").value(assignedBy)
                            .key("StatusId").value(6)
                            .key("IsSubTask").value(1)
                            .key("Comments").value(comments_updated)
                            .key("CreatedBy").value(createdBy)
                            .endObject()
                            .endObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("Json", String.valueOf(userJson));
            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);
                Log.d("res", response);

                if (response != null) {

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("UpdateAssignedTaskResult");

                        //Save userid and username if success
                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return check;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {

                if(result.equals("Reject")) {
                    new AssignTask().execute();
                }else{
                    Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ApprovalDetailsActivity.this, MainActivity.class);
                    startActivity(i);
                }
            } else {
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AssignTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/AssignTask");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // Build JSON string
            JSONStringer userJson = null;
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("taskDetails")
                        .object()
                        .key("TaskId").value(id)
                        .key("AssignedToId").value(assignedTo_st)
                        .key("AssignedById").value(assignedBy)
                        .key("StatusId").value(1)
                        .key("IsSubTask").value(1)
                        .key("Comments").value(comments_updated)
                        .key("CreatedBy").value(createdBy)
                        .endObject()
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Json", String.valueOf(userJson));
            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);
                Log.d("res", response);
                if(response != null){

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("AssignTaskResult");

                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(response_json == 200){
                Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ApprovalDetailsActivity.this, MainActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem item_noti = menu.findItem(R.id.action_noti);
        MenuItem item_logOut = menu.findItem(R.id.action_logOut);

        item_logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                return false;
            }
        });

        item_noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent i = new Intent(ApprovalDetailsActivity.this, NotificationActivity.class);
                startActivity(i);
                return false;
            }
        });

        return true;
    }
}
