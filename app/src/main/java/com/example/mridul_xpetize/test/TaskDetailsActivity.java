package com.example.mridul_xpetize.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskDetailsActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    private static String TAG_CHECK = "check";
    ArrayList<HashMap<String, String>> taskList;
    ListView subtaskList;
    TextView task_header;
    ImageButton camera;
    private Drawer result = null;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Inspector1").withEmail("inspector1@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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
                })
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        camera = (ImageButton) findViewById(R.id.imageButton_camera);
        taskList = new ArrayList<HashMap<String, String>>();
        imageView = (ImageView) findViewById(R.id.imageView);
        subtaskList = (ListView) findViewById(R.id.listView_subtask);
        task_header = (TextView) findViewById(R.id.txt_task_header);

        //Camera onClick Listener
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //ImageView onClick Listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (path != null) {

                    Intent i = new Intent(TaskDetailsActivity.this, FullImageActivity.class);
                    i.putExtra("path", path);
                    i.putExtra("type","path");
                    startActivity(i);

//                    File f = new File(path);
//                    String imageName = f.getName();
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + path), imageName);
//                    startActivity(intent);
                }
            }
        });

        //Get Intent and show in textview
        Intent i = getIntent();
        String task = i.getStringExtra("task");
        task_header.setText(task);

        //Load sub tasks
        new LoadSubTask().execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            path = getOriginalImagePath();
        }
    }

    public String getOriginalImagePath() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = TaskDetailsActivity.this.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        int column_index_data = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();

        return cursor.getString(column_index_data);
    }

    //AsyncTask to get SubTasks(to be edited)
    private class LoadSubTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TaskDetailsActivity.this);
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
//
//            Log.d("Response: ", "> " + jsonStr);
//
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
            contact.put(TAG_CHECK, "subtask1");
            taskList.add(contact);

            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(TAG_CHECK, "subtask2");
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
                    TaskDetailsActivity.this, taskList,
                    R.layout.layout_subtask, new String[]{TAG_CHECK
            }, new int[]{R.id.checkBox_subtask
            });

            subtaskList.setAdapter(adapter);
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
