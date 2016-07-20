package com.example.mridul_xpetize.test;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialize.holder.StringHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    ImageButton myTasks, workers, notification, logout;
    PreferencesHelper pref;
    private Drawer result = null;
    RelativeLayout dash_rel;

    private int count;
    ProgressDialog pDialog;
    ArrayList<HashMap<String, Object>> dataList;
    List<String> popupList = new ArrayList<String>();
    ArrayList<Integer> posList = new ArrayList<Integer>();
    ArrayList<String> nameList = new ArrayList<String>();

    int mSelectedItem;
    LayoutInflater inflater;
    MenuItem menuItem;
    ListView hidden_not;
    String popUpContents[];
    CustomAdapter cardAdapter;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        pref = new PreferencesHelper(DashboardActivity.this);
        String name = pref.GetPreferences("UserName");

        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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
        dash_rel = (RelativeLayout) findViewById(R.id.dashboard_layout);
        pref = new PreferencesHelper(DashboardActivity.this);
        hidden_not = (ListView) findViewById(R.id.listView_hidden_notification);
        dataList = new ArrayList<HashMap<String, Object>>();
        myTasks = (ImageButton) findViewById(R.id.imageButton_myTasks);
        workers = (ImageButton) findViewById(R.id.imageButton_workers);
        ImageButton approvedTask = (ImageButton) findViewById(R.id.imageButton_approval);

        //onItemClick of ListView item
        hidden_not.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                count--;
                if (count <= 0) {
                    count = 0;
                }

                menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));

                TinyDB tinydb = new TinyDB(DashboardActivity.this);
                posList.add(position);
                tinydb.putListInt("Positions", posList);
                parent.getChildAt(position - hidden_not.getFirstVisiblePosition()).setBackgroundColor(Color.TRANSPARENT);
                String desc = ((TextView) view.findViewById(R.id.textview_noti)).getText().toString();
                String byId = ((TextView) view.findViewById(R.id.noti_by)).getText().toString();
                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
                i.putExtra("Description", desc);
                i.putExtra("ById", byId);
                startActivity(i);
            }
        });

        //Hide ListView when clicked anywhere else
        dash_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hidden_not.getVisibility() == View.VISIBLE) {

                    hidden_not.setVisibility(View.GONE);

                }
            }
        });

        //onClick of approvedTask button
        approvedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, ApprovalActivity.class);
                startActivity(i);
            }
        });

        //onClick of myTasks
        myTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //onClick of workers
        workers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, WorkerListActivity.class);
                startActivity(i);
            }
        });

        new GetNotiList().execute();

    }

    //Alert Dialog
    public void dialogBox() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do You Want to Log Out ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Exit
                        System.exit(0);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView not, isRead, byName, taskName;
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
                viewHolder.taskName = (TextView) convertView.findViewById(R.id.noti_task);
                viewHolder.byName = (TextView) convertView.findViewById(R.id.noti_by);
                viewHolder.noti_linear = (LinearLayout) convertView.findViewById(R.id.not_layout);
                viewHolder.not = (TextView) convertView.findViewById(R.id.textview_noti);
                viewHolder.isRead = (TextView) convertView.findViewById(R.id.textview_isRead);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.byName.setText(dataList.get(position).get("UserName").toString());
            viewHolder.taskName.setText(dataList.get(position).get("TaskName").toString());
            viewHolder.not.setText(dataList.get(position).get("Description").toString());
            viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);

            for (int i = 0; i < posList.size(); i++) {
                Log.d("Test Custom", String.valueOf(posList.get(i)));
                if (position == posList.get(i)) {
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
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String user_id = pref.GetPreferences("UserId");

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String url = getString(R.string.url) + "EagleXpetizeService.svc/Notifications/" + user_id + "/1";
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Url", url);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {

                try {

                    JSONArray tasks = new JSONArray(jsonStr);

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String taskName = c.getString("TaskName");
                        String username = c.getString("UserName");
                        String description = c.getString("Description");
                        String byId = c.getString("ById");
                        String toId = c.getString("ToId");
                        String isNew = c.getString("IsNew");

                        // adding each child node to HashMap key => value
                        HashMap<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("TaskId", id);
                        taskMap.put("TaskName", taskName);
                        taskMap.put("UserName", username);
                        taskMap.put("Description", description);
                        taskMap.put("ById", byId);
                        taskMap.put("ToId", toId);
                        taskMap.put("IsNew", isNew);
                        dataList.add(taskMap);
                        popupList.add(description);
                        nameList.add(description);

                    }
                    count = dataList.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();

            TinyDB db = new TinyDB(DashboardActivity.this);
            db.putListString("Description", nameList);
            // initialize pop up window
//            popupWindow = popupWindow();


//            LayoutInflater factory = LayoutInflater.from(DashboardActivity.this);
//            final View addView = factory.inflate(
//                    R.layout.popup_layout, null);
//            final android.app.AlertDialog addDialog = new android.app.AlertDialog.Builder(DashboardActivity.this).create();
//            addDialog.setView(addView);


            cardAdapter = new CustomAdapter(DashboardActivity.this, R.layout.popup_layout, dataList);
//            ListView notificationList = (ListView) addView.findViewById(R.id.listView_noti);
//            notificationList.setAdapter(cardAdapter);
            hidden_not.setAdapter(cardAdapter);

//            notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    count--;
//                    if (count <= 0) {
//                        count = 0;
//                    }
//
//                    TinyDB tinydb = new TinyDB(DashboardActivity.this);
//                    menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));
//                    posList.add(position);
//                    tinydb.putListInt("Positions", posList);
////                    parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
//                    String desc = ((TextView) view.findViewById(R.id.textview_noti)).getText().toString();
//                    String byId = ((TextView) view.findViewById(R.id.noti_by)).getText().toString();
//                    Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
//                    i.putExtra("Description", desc);
//                    i.putExtra("ById", byId);
//                    startActivity(i);
//                }
//            });

//            addDialog.show();
        }
    }

    public PopupWindow popupWindow() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewDogs = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewDogs.setAdapter(cardAdapter);

        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewDogs);

        return popupWindow;
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

    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (hidden_not.getVisibility() == View.VISIBLE) {
                    hidden_not.setVisibility(View.GONE);
                } else {
                    hidden_not.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

//        MenuItem item = menu.findItem(R.id.badge);
//        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
//        View view = MenuItemCompat.getActionView(item);
//        notifCount = (Button)view.findViewById(R.id.notif_count);
//        notifCount.setText(String.valueOf(mNotifCount));
//
//        // Get the notifications MenuItem and LayerDrawable (layer-list)
////        MenuItem item_noti = menu.findItem(R.id.action_noti);
//        MenuItem item_logOut = menu.findItem(R.id.action_logOut);
//
//        item_logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//
//                return false;
//            }
//        });
//
////        item_noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////            @Override
////            public boolean onMenuItemClick(MenuItem item) {
////
////                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
////                startActivity(i);
////                return false;
////            }
////        });

        return true;
    }

    @Override
    public void onBackPressed() {

        if (hidden_not.getVisibility() == View.VISIBLE) {
            hidden_not.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
