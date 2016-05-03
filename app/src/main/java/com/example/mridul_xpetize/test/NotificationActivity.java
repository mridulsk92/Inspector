package com.example.mridul_xpetize.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Drawer result = null;

    ProgressDialog pDialog;
    private static String TAG_TASKID = "TaskId";
    private static String TAG_DESCRIPTION = "Description";
    private static String TAG_STARTDATE = "TaskStartDate";
    private static String TAG_ENDDATE = "TaskEndDate";
    private static String TAG_STATUS = "Status";
    ArrayList<HashMap<String, String>> dataList;
    ListView inspector_list;
    JSONArray workers;
    private static String TAG_USERNAME = "Username";
    private static String TAG_ID = "Id";
    PreferencesHelper pref;
    JSONArray tasks;
    String statusString;

    List<String> dbListName = new ArrayList<String>();
    List<String> dbListId = new ArrayList<String>();
    List<String> savedList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        pref = new PreferencesHelper(NotificationActivity.this);
        String name = pref.GetPreferences("Name");

        //Side Drawer Header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        //Side Drawer contents
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {

                        }
                        return false;
                    }
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        dataList = new ArrayList<HashMap<String, String>>();
        inspector_list = (ListView) findViewById(R.id.listView_workers);

//        if (isNetworkAvailable() && !savedList.isEmpty()) {
//            new GetWorkerList().execute();
//        } else {
//            GetSavedWorkerList();
//        }

        new GetTaskList().execute();

        //onItem click listener for list items
        inspector_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String username = ((TextView) view.findViewById(R.id.username)).getText().toString();
                String task_id = ((TextView) view.findViewById(R.id.task_id)).getText().toString();

                Intent intent = new Intent(NotificationActivity.this, CompletedTaskActivity.class);
                intent.putExtra("name", username);
                intent.putExtra("id", task_id);
                startActivity(intent);

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) NotificationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //AsyncTask to get tasks(to be edited)
    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            pDialog = new ProgressDialog(NotificationActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_GetCompTsk&Para=" + 3;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    tasks = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String taskID = c.getString(TAG_TASKID);
                        String username = c.getString(TAG_USERNAME);
                        String start_og = c.getString(TAG_STARTDATE);
                        String end_og = c.getString(TAG_ENDDATE);
                        int status = c.getInt(TAG_STATUS);
                        if (status == 0) {
                            statusString = "Pending Approval";
                        }

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_USERNAME, "Username : " + username);
                        contact.put(TAG_ID, id);
                        contact.put(TAG_TASKID, taskID);
                        contact.put(TAG_STARTDATE, "Start Date : " + start_og);
                        contact.put(TAG_ENDDATE, "End Date : " + end_og);
                        contact.put(TAG_STATUS, "Status : " + statusString);
                        dataList.add(contact);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    NotificationActivity.this, dataList,
                    R.layout.task_list, new String[]{TAG_USERNAME, TAG_ID, TAG_TASKID, TAG_STARTDATE, TAG_ENDDATE, TAG_STATUS},
                    new int[]{R.id.username, R.id.id, R.id.task_id, R.id.start, R.id.end, R.id.status});

            inspector_list.setAdapter(adapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }
}
