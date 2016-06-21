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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkerListActivity extends AppCompatActivity {

    private Drawer result = null;

    ProgressDialog pDialog;
    private static String TAG_INSPECTOR = "insp";
    ArrayList<HashMap<String, String>> dataList;
    ListView worker_list;
    JSONArray workers;
    private static String TAG_NAME = "UserName";
    private static String TAG_ID = "UserId";
    PreferencesHelper pref;

    List<String> dbListName = new ArrayList<String>();
    List<String> dbListId = new ArrayList<String>();
    List<String> savedList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo_ic);

        pref = new PreferencesHelper(WorkerListActivity.this);
        String name = pref.GetPreferences("UserName");

        //Side Drawer Header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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

                        if(drawerItem != null){

                        }
                        return false;
                    }
                }).build();

        //ToggleButton on Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        dataList = new ArrayList<HashMap<String, String>>();
        worker_list = (ListView) findViewById(R.id.listView_workers);

        new GetWorkerList().execute();

        //onItem click listener for list items
        worker_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = ((TextView) view.findViewById(R.id.inspector)).getText().toString();
                String worker_id = ((TextView) view.findViewById(R.id.worker_id)).getText().toString();

                Intent intent = new Intent(WorkerListActivity.this, WorkerActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("id", worker_id);
                startActivity(intent);

            }
        });
    }

    //AsyncTask to get rejected workers(to be edited)
    private class GetWorkerList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(WorkerListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = getString(R.string.url)+"EagleXpetizeService.svc/UsersListByType/Worker";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    workers = new JSONArray(jsonStr);

                    // looping through Array
                    for (int i = 0; i < workers.length(); i++) {
                        JSONObject c = workers.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_INSPECTOR, name);
                        contact.put(TAG_ID,id);
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
                    WorkerListActivity.this, dataList,
                    R.layout.layout_worker, new String[]{TAG_INSPECTOR, TAG_ID}, new int[]{R.id.inspector,R.id.worker_id
            });

            worker_list.setAdapter(adapter);
        }
    }
}
