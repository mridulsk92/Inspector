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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;
    ProgressDialog pDialog;
    private static String TAG_DESC = "desc";
    private static String TAG_TYPE = "type";
    private static String TAG_LOC = "location";
    private static String TAG_START = "start";
    private static String TAG_END = "end";
    JSONArray tasks = null;
    ListView task_list;
    ArrayList<HashMap<String, String>> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //Swipe Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("My Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("Workers"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //View Pager for swiping tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        //Side Drawer Header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Inspector1").withEmail("inspector1@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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
                        new PrimaryDrawerItem().withName("Filter").withIcon(getResources().getDrawable(R.drawable.filter_ic)).withIdentifier(1).withSelectable(false),
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if(drawerItem != null){
                            if(drawerItem.getIdentifier() == 1){
                                FilterTasks();
                            }
                        }
                        return false;
                    }
                }).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise Views
        task_list = (ListView)findViewById(R.id.listView_tasks);
        taskList = new ArrayList<HashMap<String, String>>();

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

    //Asynctask to get New Tasks(to be edited)
    private class GetNewTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            taskList.clear();
            pDialog = new ProgressDialog(MainActivity.this);
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

            // adding each child node to HashMap key => value
            contact.put(TAG_DESC, "Description 1 ");
            contact.put(TAG_LOC, "Location : loc1");
            contact.put(TAG_TYPE, "Type : type1");
            contact.put(TAG_START, "Start Date : 3/3/2016");
            contact.put(TAG_END, "End Date : 10/3/2016");
            taskList.add(contact);

            HashMap<String, String> temp = new HashMap<String, String>();

            // adding contact to contact list
            temp.put(TAG_DESC, "Description 2 ");
            temp.put(TAG_LOC, "Location : loc3");
            temp.put(TAG_TYPE, "Type : type3");
            temp.put(TAG_START, "Start Date : 4/3/2016");
            temp.put(TAG_END, "End Date : 11/3/2016");
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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, taskList,
                    R.layout.task_list, new String[]{TAG_DESC, TAG_LOC,
                    TAG_TYPE, TAG_START, TAG_END}, new int[]{R.id.desc,
                    R.id.location, R.id.type, R.id.start, R.id.end});

            task_list.setAdapter(adapter);

        }
    }

    //AsyncTask to get rejected tasks(to be edited)
    private class GetRejectedTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            taskList.clear();
            pDialog = new ProgressDialog(MainActivity.this);
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

            // adding each child node to HashMap key => value
            contact.put(TAG_DESC, "Description 2");
            contact.put(TAG_LOC, "Location : loc2");
            contact.put(TAG_TYPE, "Type : type2");
            contact.put(TAG_START, "Start Date : 3/2/2016");
            contact.put(TAG_END, "End Date : 10/2/2016");

            // adding contact to contact list
            taskList.add(contact);
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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, taskList,
                    R.layout.task_list, new String[]{TAG_DESC, TAG_LOC,
                    TAG_TYPE, TAG_START, TAG_END}, new int[]{R.id.desc,
                    R.id.location, R.id.type, R.id.start, R.id.end});

            task_list.setAdapter(adapter);
        }
    }

}
