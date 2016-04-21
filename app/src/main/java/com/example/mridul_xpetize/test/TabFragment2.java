package com.example.mridul_xpetize.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;

public class TabFragment2 extends Fragment {

    ProgressDialog pDialog;
    private static String TAG_INSPECTOR = "insp";
    ArrayList<HashMap<String, String>> dataList;
    ListView inspector_list;
    JSONArray workers;
    private static String TAG_NAME = "Name";
    private static String TAG_ID = "Id";

    List<String> dbListName = new ArrayList<String>();
    List<String> dbListId = new ArrayList<String>();
    List<String> savedList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_fragment2, container, false);
        dataList = new ArrayList<HashMap<String, String>>();
        inspector_list = (ListView) view.findViewById(R.id.listView_workers);

//        if (isNetworkAvailable() && !savedList.isEmpty()) {
//            new GetWorkerList().execute();
//        } else {
//            GetSavedWorkerList();
//        }

        new GetWorkerList().execute();

        //onItem click listener for list items
        inspector_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = ((TextView) view.findViewById(R.id.inspector)).getText().toString();
                String worker_id = ((TextView)view.findViewById(R.id.worker_id)).getText().toString();

                Intent intent = new Intent(getActivity(), WorkerActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("id",worker_id);
                startActivity(intent);

//                Intent i = new Intent(getActivity(), WorkerActivity.class);
//                i.putExtra("name", name);
//                startActivity(i);
            }
        });

        return view;
    }

    private void GetSavedWorkerList() {

        TinyDB tiny = new TinyDB(getActivity());
        savedList = tiny.getListString("Names");

        for (int i = 0; i < savedList.size(); i++) {

            String name = savedList.get(i);
            HashMap<String, String> contact = new HashMap<String, String>();

            // adding each child node to HashMap key => value
            contact.put(TAG_INSPECTOR, name);
            dataList.add(contact);
        }

        ListAdapter adapter = new SimpleAdapter(
                getActivity(), dataList,
                R.layout.layout_worker, new String[]{TAG_INSPECTOR}, new int[]{R.id.inspector,
        });

        inspector_list.setAdapter(adapter);

    }

    //AsyncTask to get rejected workers(to be edited)
    private class GetWorkerList extends AsyncTask<Void, Void, Void> {

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

            String url = "http://vikray.in/MyService.asmx/ExcProcedure?Para=Proc_GetUserMst&Para=3";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    workers = new JSONArray(jsonStr);
                    // looping through All Contacts
                    for (int i = 0; i < workers.length(); i++) {
                        JSONObject c = workers.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);

                        dbListName.add(name);
                        dbListId.add(id);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_INSPECTOR, name);
                        contact.put(TAG_ID,id);
                        dataList.add(contact);

                    }
                    TinyDB tiny = new TinyDB(getActivity());
                    tiny.putListString("Names", (ArrayList<String>) dbListName);

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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), dataList,
                    R.layout.layout_worker, new String[]{TAG_INSPECTOR, TAG_ID}, new int[]{R.id.inspector,R.id.worker_id
            });

            inspector_list.setAdapter(adapter);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}