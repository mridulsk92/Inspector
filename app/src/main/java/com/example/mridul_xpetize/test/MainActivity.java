package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    LayoutInflater inflater;
    CustomAdapter cardAdapter;

    JSONArray tasks;

    private static String TAG_DESCRIPTION = "Description";
    private static String TAG_ID = "Id";
    private static String TAG_STARTDATE = "TaskStartDate";
    private static String TAG_ENDDATE = "TaskEndDate";
    private static String TAG_PRIORITY = "TaskPriority";

    ArrayList<HashMap<String, Object>> dataList;
    ArrayList<HashMap<String, Object>> highPriorityList;
    ArrayList<HashMap<String, Object>> mediumPriorityList;
    ArrayList<HashMap<String, Object>> lowPriorityList;


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
        dataList = new ArrayList<HashMap<String, Object>>();
        highPriorityList = new ArrayList<HashMap<String, Object>>();
        mediumPriorityList = new ArrayList<HashMap<String, Object>>();
        lowPriorityList = new ArrayList<HashMap<String, Object>>();

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
                                cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, highPriorityList);
                                task_list.setAdapter(cardAdapter);

                            } else if (drawerItem.getIdentifier() == 4) {

                                //Load Medium priority tasks
                                cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, mediumPriorityList);
                                task_list.setAdapter(cardAdapter);

                            } else if (drawerItem.getIdentifier() == 5) {

                                //Load low priority tasks
                                cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, lowPriorityList);
                                task_list.setAdapter(cardAdapter);

                            } else if (drawerItem.getIdentifier() == 6) {

                                //Load all tasks
                                cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, dataList);
                                task_list.setAdapter(cardAdapter);
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
                String id_task = ((TextView)view.findViewById(R.id.id)).getText().toString();

                //Pass the Strings to the next Activity
                Intent i = new Intent(MainActivity.this, TaskActivity.class);
                i.putExtra("desc", desc);
                i.putExtra("type", type);
                i.putExtra("loc", loc);
                i.putExtra("start", start);
                i.putExtra("id",id_task);
                i.putExtra("end", end);
                startActivity(i);
            }
        });

//        new GetNewTasks().execute();
        new GetTaskList().execute();
    }


    //Define Custom Adapter for Message Cards
    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView status, desc, priority, startdate, enddate, loc, id;
            CardView cv;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.priority = (TextView) convertView.findViewById(R.id.priority);
                viewHolder.startdate = (TextView) convertView.findViewById(R.id.start);
                viewHolder.enddate = (TextView) convertView.findViewById(R.id.end);
                viewHolder.loc = (TextView) convertView.findViewById(R.id.location);
                viewHolder.id = (TextView) convertView.findViewById(R.id.task_id);
                viewHolder.cv = (CardView) convertView.findViewById(R.id.card_tasks);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
//            viewHolder.status.setText(dataList.get(position).get("Status").toString());
            viewHolder.desc.setText(dataList.get(position).get("Description").toString());
            viewHolder.priority.setText(dataList.get(position).get("TaskPriority").toString());
            viewHolder.startdate.setText(dataList.get(position).get("TaskStartDate").toString());
            viewHolder.enddate.setText(dataList.get(position).get("TaskEndDate").toString());
            viewHolder.id.setText(dataList.get(position).get("Id").toString());
            return convertView;
        }
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
            String url = getString(R.string.url)+"MyService.asmx/ExcProcedure?Para=Proc_GetTaskMst&Para=" + userid;

//            String url = "http://10.0.2.2/OnlineSalesService/OnlineSalesService.svc/GetPropertyNames";
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

                            HashMap<String, Object> tempHigh = new HashMap<String, Object>();

                            tempHigh.put(TAG_DESCRIPTION, "Description : " + desc);
                            tempHigh.put(TAG_ID, id);
                            tempHigh.put(TAG_STARTDATE, "Start Date : " + st_date);
                            tempHigh.put(TAG_ENDDATE, "End Date : " + end_date);
                            tempHigh.put(TAG_PRIORITY, "Priority : " + priority_string);
                            highPriorityList.add(tempHigh);

                        } else if (priority == 2) {

                            priority_string = "Medium";

                            HashMap<String, Object> tempMedium = new HashMap<String, Object>();

                            tempMedium.put(TAG_DESCRIPTION, "Description : " + desc);
                            tempMedium.put(TAG_ID, id);
                            tempMedium.put(TAG_STARTDATE, "Start Date : " + st_date);
                            tempMedium.put(TAG_ENDDATE, "End Date : " + end_date);

                            mediumPriorityList.add(tempMedium);

                        } else if (priority == 3) {

                            priority_string = "Low";

                            HashMap<String, Object> tempLow = new HashMap<String, Object>();

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
                        HashMap<String, Object> contact = new HashMap<String, Object>();

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

            cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, dataList);
            task_list.setAdapter(cardAdapter);
        }
    }
}
