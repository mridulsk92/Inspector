package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskActivity extends AppCompatActivity {

    TextView desc, type, loc, start, end;
    ListView checkList;
    Button submit;
    ProgressDialog pDialog;
    private static String TAG_CHECK = "check";
    ArrayList<HashMap<String, String>> taskList;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    TextView task, status;
    private Drawer result = null;
    PreferencesHelper pref;
    String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //Initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        pref = new PreferencesHelper(TaskActivity.this);
        String name = pref.GetPreferences("Name");

        //Side Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

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
                            if (drawerItem.getIdentifier() == 1) {
                            }
                        }
                        return false;
                    }
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        submit = (Button) findViewById(R.id.button_submit);
        taskList = new ArrayList<HashMap<String, String>>();
        checkList = (ListView) findViewById(R.id.listView_check);
        desc = (TextView) findViewById(R.id.desc);
        type = (TextView) findViewById(R.id.type);
        loc = (TextView) findViewById(R.id.location);
        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end);

        //Get data coming from the main activity
        Intent i = getIntent();
        String desc_st = i.getStringExtra("desc");
        String type_st = i.getStringExtra("type");
        String loc_st = i.getStringExtra("loc");
        String start_st = i.getStringExtra("start");
        String end_st = i.getStringExtra("end");
        taskId = i.getStringExtra("id");

        //Display data from main activity
        desc.setText(desc_st);
        type.setText(type_st);
        loc.setText(loc_st);
        start.setText(start_st);
        end.setText(end_st);

        //Get Tasks data
        new LoadCheckList().execute();

        //TaskList onClick Item
        checkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String task = ((TextView) view.findViewById(R.id.text_task)).getText().toString();
                Intent i = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                i.putExtra("task", task);
                startActivity(i);
            }
        });

        //submit button on click
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Alert Dialog box to show confirmation
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TaskActivity.this);
                CharSequence[] cs = selectedStrings.toArray(new CharSequence[selectedStrings.size()]);
                builder1.setTitle("Confirm");
                builder1.setMessage("Are you sure ?");
                builder1.setItems(cs, null);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                selectedStrings.clear();
                                new PostTasks().execute();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                selectedStrings.clear();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    //AsyncTask to get Tasks(to be edited)
    private class LoadCheckList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TaskActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
//            ServiceHandler sh = new ServiceHandler();
//
//            // Making a request to url and getting response
//            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

//            Log.d("Response: ", "> " + jsonStr);

//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//
//                    // Getting JSON Array node
//                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);
//
//                    // looping through All Contacts
//                    for (int i = 0; i < contacts.length(); i++) {
//                        JSONObject c = contacts.getJSONObject(i);
//
//                        String id = c.getString(TAG_ID);
//                        String name = c.getString(TAG_NAME);
//                        String email = c.getString(TAG_EMAIL);
//                        String address = c.getString(TAG_ADDRESS);
//                        String gender = c.getString(TAG_GENDER);
//
//                        // Phone node is JSON Object
//                        JSONObject phone = c.getJSONObject(TAG_PHONE);
//                        String mobile = phone.getString(TAG_PHONE_MOBILE);
//                        String home = phone.getString(TAG_PHONE_HOME);
//                        String office = phone.getString(TAG_PHONE_OFFICE);

            // tmp hashmap for single contact
            HashMap<String, String> contact = new HashMap<String, String>();
            contact.put(TAG_CHECK, "task1");
            taskList.add(contact);

            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(TAG_CHECK, "task2");
            taskList.add(temp);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    TaskActivity.this, taskList,
                    R.layout.layout_checklist, new String[]{TAG_CHECK
            }, new int[]{R.id.text_task
            });

            checkList.setAdapter(adapter);
        }
    }

    private class PostTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TaskActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String user_id = pref.GetPreferences("Designation");
            String username = pref.GetPreferences("Name");
            int status = 0;
//            String stDate = start_st.replaceAll("\\s+", "");
//            String endDate = end_st.replaceAll("\\s+", "");
//            Log.d("Replaced", stDate);
            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_PostTaskMst&Para=" + taskId + "&Para=" + user_id + "&Para=" + status + "&Para=" + username;
            // Making a request to url and getting response

            Log.d("Test", url);

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

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
