package com.example.mridul_xpetize.test;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment1 extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment1, container, false);

        task_list = (ListView) view.findViewById(R.id.listView_tasks);
        dataList = new ArrayList<HashMap<String, String>>();
        taskList = new ArrayList<HashMap<String, String>>();
        pref = new PreferencesHelper(getActivity());

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
                Intent i = new Intent(getActivity(), TaskActivity.class);
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
        return view;
    }

    //Asynctask to get New Tasks(to be edited)
    private class GetNewTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            taskList.clear();
            pDialog = new ProgressDialog(getActivity());
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
                    getActivity(), taskList,
                    R.layout.task_list, new String[]{TAG_DESC, TAG_LOC,
                    TAG_TYPE, TAG_START, TAG_END}, new int[]{R.id.desc,
                    R.id.location, R.id.type, R.id.start, R.id.end});

            task_list.setAdapter(adapter);
        }
    }

    //AsyncTask to get rejected tasks(to be edited)
    private class GetTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            pDialog = new ProgressDialog(getActivity());
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
                        } else if (priority == 2) {
                            priority_string = "Medium";
                        } else if (priority == 3) {
                            priority_string = "Low";
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
                    getActivity(), dataList,
                    R.layout.task_list, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                    new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

            task_list.setAdapter(adapter);
        }
    }
}