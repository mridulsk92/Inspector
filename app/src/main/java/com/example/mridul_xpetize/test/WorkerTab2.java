package com.example.mridul_xpetize.test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorkerTab2 extends Fragment {

    ListView task_list;
    TextView empty, workerName;
    ImageButton add_task, startCal, endCal;

    String desc, stdate, enddate, worker_id;
    int priority;

    Calendar myCalendarS, myCalendarE;
    EditText startDate, endDate;

    private static String TAG_DESCRIPTION = "Description";
    private static String TAG_ID = "Id";
    private static String TAG_STARTDATE = "TaskStartDate";
    private static String TAG_ENDDATE = "TaskEndDate";
    private static String TAG_PRIORITY = "TaskPriority";
    ListView added_list;
    JSONArray tasks;

    ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_worker_tab2, container, false);

        WorkerActivity activity = (WorkerActivity) getActivity();
        String name = activity.getIntent().getExtras().getString("name");
        worker_id = activity.getIntent().getExtras().getString("id");

        //Initialize
        workerName = (TextView)view.findViewById(R.id.textView_inspector);
        task_list = (ListView)view.findViewById(R.id.listView_task);
        task_list.setEmptyView(view.findViewById(android.R.id.empty));
        add_task = (ImageButton)view.findViewById(R.id.imageButton_add);
        dataList = new ArrayList<HashMap<String, String>>();
        empty = (TextView)view.findViewById(R.id.empty);
        added_list = (ListView)view.findViewById(R.id.listView_task);

        workerName.setText(name);

        //onClick of ImageButton
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddTask();
            }
        });

        new GetTaskList().execute();
        return view;
    }


    private void AddTask() {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View addView = factory.inflate(
                R.layout.addtask_dialog_new, null);
        final AlertDialog addDialog = new AlertDialog.Builder(getActivity()).create();
        addDialog.setView(addView);

        //Initialise
        startCal = (ImageButton) addView.findViewById(R.id.imageButton_startDate);
        endCal = (ImageButton) addView.findViewById(R.id.imageButton_endDate);
        myCalendarS = Calendar.getInstance();
        myCalendarE = Calendar.getInstance();
        final EditText editDescription = (EditText) addView.findViewById(R.id.editText_desc);
        final Spinner typeSpinner = (Spinner) addView.findViewById(R.id.spinner_type);
        startDate = (EditText) addView.findViewById(R.id.editText_start);
        endDate = (EditText) addView.findViewById(R.id.editText_end);
        final EditText loc = (EditText) addView.findViewById(R.id.editText_location);

        //Add button onClick
        Button addTask = (Button) addView.findViewById(R.id.button_add);
        addTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                desc = editDescription.getText().toString();
                stdate = startDate.getText().toString();
                enddate = endDate.getText().toString();
                if (typeSpinner.getSelectedItem().equals("High")) {
                    priority = 1;
                } else if (typeSpinner.getSelectedItem().equals("Medium")) {
                    priority = 2;
                } else if (typeSpinner.getSelectedItem().equals("Low")) {
                    priority = 3;
                } else {
                    priority = 1;
                }

                addDialog.dismiss();
                new PostTasks().execute();

            }
        });

        //Date Picker
        final DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarS.set(Calendar.YEAR, year);
                myCalendarS.set(Calendar.MONTH, monthOfYear);
                myCalendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarE.set(Calendar.YEAR, year);
                myCalendarE.set(Calendar.MONTH, monthOfYear);
                myCalendarE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelEnd();
            }
        };

        startCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), dateStart, myCalendarS
                        .get(Calendar.YEAR), myCalendarS.get(Calendar.MONTH),
                        myCalendarS.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), dateEnd, myCalendarE
                        .get(Calendar.YEAR), myCalendarE.get(Calendar.MONTH),
                        myCalendarE.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Spinner
        ArrayList<String> spinnerData = new ArrayList<String>();
        spinnerData.add("Select Priority");
        spinnerData.add("High");
        spinnerData.add("Medium");
        spinnerData.add("Low");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerData);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        typeSpinner.setAdapter(dataAdapter);

//            if(typeSpinner.getSelectedItemPosition() == 0){
//                //Do Nothing
//            }else{
//                String type_selected = typeSpinner.getSelectedItem().toString();
//            }
        addDialog.show();

    }

    private void updateLabelStart() {

        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(myCalendarS.getTime()));
    }

    private void updateLabelEnd() {

        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setText(sdf.format(myCalendarE.getTime()));
    }

    private class PostTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_InsertTaskMst&Para=" + desc + "&Para=" + worker_id + "&Para=2&Para=" + stdate + "&Para=" + enddate + "&Para=" + 3 + "&Para=" + priority + "&Para=" + 2;
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

            new GetTaskList().execute();

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

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_GetTaskMst&Para=" + worker_id;
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

            empty.setVisibility(View.GONE);
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), dataList,
                    R.layout.layput_tasks, new String[]{TAG_DESCRIPTION, TAG_ID, TAG_STARTDATE, TAG_ENDDATE, TAG_PRIORITY},
                    new int[]{R.id.desc, R.id.task_id, R.id.start, R.id.end, R.id.priority});

            added_list.setAdapter(adapter);
        }
    }
}
