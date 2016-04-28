package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;

    ListView task_list;
    ProgressDialog pDialog;
    private static String TAG_DESC = "desc";
    private static String TAG_TYPE = "type";
    private static String TAG_LOC = "location";
    private static String TAG_START = "start";
    private static String TAG_END = "end";
    ArrayList<HashMap<String, String>> taskList;
    PreferencesHelper pref;

    JSONArray tasks;

    private static String TAG_DESCRIPTION = "Description";
    private static String TAG_ID = "Id";
    private static String TAG_STARTDATE = "TaskStartDate";
    private static String TAG_ENDDATE = "TaskEndDate";
    private static String TAG_PRIORITY = "TaskPriority";

    ArrayList<HashMap<String, String>> dataList;
    ArrayList<HashMap<String, String>> highPriorityList;
    ArrayList<HashMap<String, String>> mediumPriorityList;
    ArrayList<HashMap<String, String>> lowPriorityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //Initialise Views
        task_list = (ListView)findViewById(R.id.listView_tasks);
        taskList = new ArrayList<HashMap<String, String>>();

        task_list = (ListView) findViewById(R.id.listView_tasks);
        dataList = new ArrayList<HashMap<String, String>>();
        highPriorityList = new ArrayList<HashMap<String, String>>();
        mediumPriorityList = new ArrayList<HashMap<String, String>>();
        lowPriorityList = new ArrayList<HashMap<String, String>>();

        taskList = new ArrayList<HashMap<String, String>>();
        pref = new PreferencesHelper(MainActivity.this);

        pref = new PreferencesHelper(MainActivity.this);
        String acc_name = pref.GetPreferences("Name");

        //Side Drawer Header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                ).build();
        new ProfileDrawerItem().withName(acc_name).withEmail(acc_name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile));

        //Side Drawer contents
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withIdentifier(1).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withIdentifier(2).withSelectable(false),
                        new SectionDrawerItem().withName("Filter"),
//                        new SecondaryDrawerItem().withName("New Task").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withSelectable(false),
                        new SecondaryDrawerItem().withName("All Task").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withIdentifier(6).withSelectable(false),
                        new SecondaryDrawerItem().withName("High Priority").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withIdentifier(3).withSelectable(false),
                        new SecondaryDrawerItem().withName("Medium Priority").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withIdentifier(4).withSelectable(false),
                        new SecondaryDrawerItem().withName("Low Priority").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withIdentifier(5).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 3) {

                                //Load high priority tasks
                                ListAdapter adapter = new SimpleAdapter(
                                        MainActivity.this, highPriorityList,
                                        R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                                        new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

                                task_list.setAdapter(adapter);
                            } else if (drawerItem.getIdentifier() == 4) {

                                //Load Medium priority tasks
                                ListAdapter adapter = new SimpleAdapter(
                                        MainActivity.this, mediumPriorityList,
                                        R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                                        new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

                                task_list.setAdapter(adapter);
                            } else if (drawerItem.getIdentifier() == 5) {

                                //Load low priority tasks
                                ListAdapter adapter = new SimpleAdapter(
                                        MainActivity.this, lowPriorityList,
                                        R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                                        new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

                                task_list.setAdapter(adapter);
                            } else if (drawerItem.getIdentifier() == 6) {

                                //Load all tasks
                                ListAdapter adapter = new SimpleAdapter(
                                        MainActivity.this, dataList,
                                        R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                                        new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

                                task_list.setAdapter(adapter);
                            }
                        }
                        return false;
                    }
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //ListView onItem Click
        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get TextView values and assign to String
                String desc = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                String type = ((TextView) view.findViewById(R.id.type)).getText().toString();
                String loc = ((TextView) view.findViewById(R.id.location)).getText().toString();
                String start = ((TextView) view.findViewById(R.id.start)).getText().toString();
                String end = ((TextView) view.findViewById(R.id.end)).getText().toString();

                //Pass the Strings to the next Activity
                Intent i = new Intent(MainActivity.this, TaskActivity.class);
                i.putExtra("desc", desc);
                i.putExtra("type", type);
                i.putExtra("loc", loc);
                i.putExtra("start", start);
                i.putExtra("end", end);
                startActivity(i);
            }
        });

//        new GetNewTasks().execute();
        new GetTaskList().execute();

    }

    //Filter task function
    private void FilterTasks() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View filterView = factory.inflate(
                R.layout.filter_dialog, null);
        final AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setView(filterView);

        //Initialise
        final RadioButton priorityHigh = (RadioButton) filterView.findViewById(R.id.priority_high);
        final RadioButton priorityLow = (RadioButton) filterView.findViewById(R.id.priority_low);
        final RadioButton dateNew = (RadioButton) filterView.findViewById(R.id.date_new);
        final RadioButton dateOld = (RadioButton) filterView.findViewById(R.id.date_old);
        final RadioButton taskNew = (RadioButton) filterView.findViewById(R.id.task_new);
        final RadioButton taskRejected = (RadioButton) filterView.findViewById(R.id.task_rejected);
        Button applyFilter = (Button) filterView.findViewById(R.id.filter_apply_btn);

        //Priority high low
        priorityHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityLow.setChecked(false);
            }
        });
        priorityLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityHigh.setChecked(false);
            }
        });

        //Date New/Old
        dateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateOld.setChecked(false);
            }
        });
        dateOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateNew.setChecked(false);
            }
        });

        //Task New/Rejected
        taskNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskRejected.setChecked(false);
            }
        });
        taskRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskNew.setChecked(false);
            }
        });

        //Apply button onClick
        applyFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Check which filter and apply
//                if (taskNew.isChecked()) {
//                    new GetNewTasks().execute();
//                    filterDialog.dismiss();
//                } else if (taskRejected.isChecked()) {
//                    new GetRejectedTasks().execute();
//                    filterDialog.dismiss();
//                } else {
//                    filterDialog.dismiss();
//                }
                filterDialog.dismiss();
            }
        });
        filterDialog.show();
    }

    //AsyncTask to get rejected tasks(to be edited)
    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            highPriorityList.clear();
            mediumPriorityList.clear();
            lowPriorityList.clear();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String userid = pref.GetPreferences("User Id");
            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_GetTaskMst&Para=" + userid;
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
                        String desc = c.getString(TAG_DESCRIPTION);
                        String st_date = c.getString(TAG_STARTDATE);
                        String end_date = c.getString(TAG_ENDDATE);
                        int priority = c.getInt(TAG_PRIORITY);
                        String priority_string = "High";
                        if (priority == 1) {

                            priority_string = "High";

                            HashMap<String, String> tempHigh = new HashMap<String, String>();

                            tempHigh.put(TAG_DESCRIPTION, "Description : " + desc);
                            tempHigh.put(TAG_ID, id);
                            tempHigh.put(TAG_STARTDATE, "Start Date : " + st_date);
                            tempHigh.put(TAG_ENDDATE, "End Date : " + end_date);
                            tempHigh.put(TAG_PRIORITY, "Priority : " + priority_string);
                            highPriorityList.add(tempHigh);

                        } else if (priority == 2) {

                            priority_string = "Medium";

                            HashMap<String, String> tempMedium = new HashMap<String, String>();

                            tempMedium.put(TAG_DESCRIPTION, "Description : " + desc);
                            tempMedium.put(TAG_ID, id);
                            tempMedium.put(TAG_STARTDATE, "Start Date : " + st_date);
                            tempMedium.put(TAG_ENDDATE, "End Date : " + end_date);
                            tempMedium.put(TAG_PRIORITY, "Priority : " + priority_string);
                            mediumPriorityList.add(tempMedium);

                        } else if (priority == 3) {

                            priority_string = "Low";

                            HashMap<String, String> tempLow = new HashMap<String, String>();

                            tempLow.put(TAG_DESCRIPTION, "Description : " + desc);
                            tempLow.put(TAG_ID, id);
                            tempLow.put(TAG_STARTDATE, "Start Date : " + st_date);
                            tempLow.put(TAG_ENDDATE, "End Date : " + end_date);
                            tempLow.put(TAG_PRIORITY, "Priority : " + priority_string);
                            lowPriorityList.add(tempLow);

                        } else {
                            priority_string = "High";
                        }


                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_DESCRIPTION, "Description : " + desc);
                        contact.put(TAG_ID, id);
                        contact.put(TAG_STARTDATE, "Start Date : " + st_date);
                        contact.put(TAG_ENDDATE, "End Date : " + end_date);
                        contact.put(TAG_PRIORITY, "Priority : " + priority_string);
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
                    MainActivity.this, dataList,
                    R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                    new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

            task_list.setAdapter(adapter);
        }
    }

}
