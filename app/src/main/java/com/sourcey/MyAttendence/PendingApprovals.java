package com.sourcey.MyAttendence;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendingApprovals extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModelJunior> data;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    boolean mload = false;

    public PendingApprovals() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_approvals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final Fragment currentFrag=this;

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);



        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModelJunior>();
        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse() {

            @Override
            public void processFinish(String output, int response) {
                if (response == 200 && !(output.isEmpty())) {
                    try {

                        JSONArray obj = new JSONArray(output);
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject attendance = obj.getJSONObject(i);
                            String name = attendance.getString("Name");
                            String position = attendance.getString("Position");
                            String contact = attendance.getString("Contact");
                            String userid = attendance.getString("Id");
                            data.add(new DataModelJunior(
                                    name,
                                    position,
                                    contact,
                                    userid
                            ));

                        }

                        adapter = new CustomAdapterJunior(data,getContext(),false,getFragmentManager(),currentFrag);
                        recyclerView.setAdapter(adapter);
                    } catch (Throwable t) {
                        Toast.makeText(getContext(), "No records found.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Kindly check your Internet connection",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String details = sharedpreferences.getString("UserDetails", "");
        JSONObject obj;
        String userid = "";
        try {
            obj = new JSONObject(details);
            userid = obj.getString("Id");
        } catch (Throwable t) {
            Log.e("App: ", "Failed to get id");
        }

        sendpost.paras.put("seniorid", userid);
        sendpost.endpoint = "/attendance/getTeamWithoutApproval";
        sendpost.method = "POST";
        sendpost.mycontext = getContext();
        sendpost.execute();
        mload =true;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mload){
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        }
    }

}

