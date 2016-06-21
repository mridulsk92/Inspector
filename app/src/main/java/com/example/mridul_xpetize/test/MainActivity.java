package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mikepenz.materialdrawer.MiniDrawer;
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

    String id_task;
    ListView task_list;
    ProgressDialog pDialog;

    PreferencesHelper pref;
    LayoutInflater inflater;
    CustomAdapter cardAdapter;

    JSONArray tasks;

    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();

    ArrayList<HashMap<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo_ic);

        //Initialise Views
        task_list = (ListView) findViewById(R.id.listView_tasks);
        dataList = new ArrayList<HashMap<String, Object>>();
        pref = new PreferencesHelper(MainActivity.this);
        String acc_name = pref.GetPreferences("UserName");

        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(acc_name).withEmail(acc_name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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

        //Add ToggleButton to ToolBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //ListView onItem Click
        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get TextView values and assign to String
                id_task = ((TextView) view.findViewById(R.id.task_id)).getText().toString();
                
                //Show SubTasks in AlertDialogBox
                CharSequence[] items = popupList.toArray(new CharSequence[popupList.size()]);
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                builderSingle.setTitle("Select A SubTask");

                //Cancel Button on AlertDialogBox
                builderSingle.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //Load SubTasks
                builderSingle.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Send selected Id's to TaskActivity
                        Intent i = new Intent(MainActivity.this, TaskActivity.class);
                        i.putExtra("SubTaskId", popupListId.get(which));
                        i.putExtra("TaskId", id_task);
                        startActivity(i);
                    }
                });
                builderSingle.show();
            }
        });

        //Load Tasks
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

            TextView status, desc, comments, startdate, enddate, loc, id;
            CardView cv;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.task_list_main, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments);
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
            viewHolder.id.setText(dataList.get(position).get("TaskId").toString());
            viewHolder.desc.setText(dataList.get(position).get("Description").toString());
            viewHolder.comments.setText(dataList.get(position).get("Comments").toString());
            viewHolder.loc.setText(dataList.get(position).get("Location").toString());
            return convertView;
        }
    }
    
    //Class to GetTasks
    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String userid = pref.GetPreferences("UserId");

            String url = getString(R.string.url) + "EagleXpetizeService.svc/Tasks/0/0";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    tasks = new JSONArray(jsonStr);
                    //Loop through Tasks
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String comments = c.getString("Comments");
                        String desc = c.getString("Description");
                        String loc = c.getString("Location");

                        // adding each child node to HashMap key => value
                        HashMap<String, Object> taskMap = new HashMap<String, Object>();

                        taskMap.put("Description", "Description : " + desc);
                        taskMap.put("TaskId", id);
                        taskMap.put("Comments", "Comments : " + comments);
                        taskMap.put("Location", "Location : " + loc);
                        dataList.add(taskMap);

                        JSONArray subTasks = c.getJSONArray("SubTasks");
                        //Loop through SubTasks
                        for (int j = 0; j < subTasks.length(); j++) {

                            JSONObject a = subTasks.getJSONObject(j);
                            String sub_id = a.getString("SubTaskId");
                            String sub_desc = a.getString("Description");

                            //Load Description and Id's in List
                            popupList.add(sub_desc);
                            popupListId.add(sub_id);
                        }

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

            //Display data in ListView
            cardAdapter = new CustomAdapter(MainActivity.this, R.layout.task_list, dataList);
            task_list.setAdapter(cardAdapter);
        }
    }
}
