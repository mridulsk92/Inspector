package com.example.mridul_xpetize.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    PreferencesHelper pref;
    LayoutInflater inflater;
    ProgressDialog pDialog;
    ListView notiList;

    ArrayList<HashMap<String, Object>> dataList;
    List<String> popupList = new ArrayList<String>();
    List<Integer> posList = new ArrayList<Integer>();
    int count;
    MenuItem menuItem;
    String desc, byId;
    TextView description, ById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

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

        //Initialise
        description = (TextView)findViewById(R.id.textView_desc);
        ById = (TextView) findViewById(R.id.textView_byId);
        dataList = new ArrayList<HashMap<String, Object>>();
//        notiList = (ListView) findViewById(R.id.listView_notification);

        //Get Intent
        Intent i = getIntent();
        desc = i.getStringExtra("Description");
        byId = i.getStringExtra("ById");

        //Set TextView Values
        description.setText(desc);
        ById.setText(byId);
    }

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }


        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public HashMap<String, Object> getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView not, isRead;
            LinearLayout noti_linear;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.noti_linear = (LinearLayout) convertView.findViewById(R.id.not_layout);
//                viewHolder.not = (TextView) convertView.findViewById(R.id.textview_noti);
//                viewHolder.isRead = (TextView) convertView.findViewById(R.id.textview_isRead);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.not.setText(dataList.get(position).get("CheckList").toString());
            viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);

            for (int i = 0; i < posList.size(); i++) {
                if (position == posList.get(i)) {
                    // set your color
                    viewHolder.noti_linear.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);
                }
            }

            return convertView;
        }
    }

    private class GetNotiList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(NotificationActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HashMap<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put("CheckList", "Notification1");
            dataList.add(taskMap);
            popupList.add("Notification1");

            HashMap<String, Object> taskMap2 = new HashMap<String, Object>();
            taskMap2.put("CheckList", "Notification2");
            dataList.add(taskMap2);
            popupList.add("Notification2");

            HashMap<String, Object> taskMap3 = new HashMap<String, Object>();
            taskMap3.put("CheckList", "Notification3");
            dataList.add(taskMap3);
            popupList.add("Notification3");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();

            count = dataList.size();
            CustomAdapter adapterNotification = new CustomAdapter(NotificationActivity.this, R.layout.notification_layout, dataList);
            notiList.setAdapter(adapterNotification);
        }
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.rel_panel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        menuItem = menu.findItem(R.id.testAction);
//        menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));

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

                return false;
            }
        });

        return true;
    }
}
