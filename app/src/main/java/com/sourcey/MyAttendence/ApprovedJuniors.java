package com.sourcey.MyAttendence;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ApprovedJuniors extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModelJunior> data;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    boolean mload =false;
    public ApprovedJuniors() {
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
        return inflater.inflate(R.layout.fragment_approved_juniors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final Fragment currentFrag=this;


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
                            if(attendance.getString("Approval").equals("1")) {
                                String name = attendance.getString("Name");
                                String position = attendance.getString("Position");
                                String contact = attendance.getString("Contact");
                                String userid = attendance.getString("Userid");
                                String stats = attendance.getString("Count");
                                data.add(new DataModelJunior(
                                        name,
                                        position,
                                        contact,
                                        userid,
                                        stats
                                ));
                            }
                        }

                        adapter = new CustomAdapterJunior(data,getContext(),true,getFragmentManager(),currentFrag);
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

      //  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        sendpost.paras.put("seniorid", userid);
        sendpost.paras.put("month", String.valueOf((cal.MONTH)-1));
        sendpost.endpoint = "/attendance/getTeamByMonth";
        sendpost.method = "POST";
        sendpost.mycontext = getContext();
        sendpost.execute();
        mload=true;

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

class CustomAdapterJunior extends RecyclerView.Adapter<CustomAdapterJunior.MyViewHolderJunior> {

    private ArrayList<DataModelJunior> dataSet;
    Context mcontext;
    boolean setVisibility;
    FragmentManager mfragment;
    Fragment frag;

    public class MyViewHolderJunior extends RecyclerView.ViewHolder {

        TextView details;
        TextView stats;
        RelativeLayout attendanceView;
        TextView button_text;



        public MyViewHolderJunior(View itemView) {
            super(itemView);
            this.details = (TextView) itemView.findViewById(R.id.tvAddress);
            this.attendanceView= (RelativeLayout) itemView.findViewById(R.id.rlPickLocation);
            this.stats=(TextView) itemView.findViewById(R.id.presentDays);
            if (!setVisibility)
                stats.setVisibility(View.GONE);
            this.button_text=(TextView) itemView.findViewById(R.id.textView);

            if (setVisibility == true)
                button_text.setText("View Attendance");
            else
                button_text.setText("Approve");
    }
    }

    public CustomAdapterJunior(ArrayList<DataModelJunior> data, Context mcontext, boolean setVisibility, FragmentManager mfragment,Fragment frag) {
        this.dataSet = data;
        this.mcontext = mcontext;
        this.setVisibility=setVisibility;
        this.mfragment=mfragment;
        this.frag=frag;
    }

    @Override
    public MyViewHolderJunior onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.junior_cardrow, parent, false);

        MyViewHolderJunior myViewHolder = new MyViewHolderJunior(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolderJunior holder, final int listPosition) {

        TextView details = holder.details;
        RelativeLayout attendanceView = holder.attendanceView;
        TextView stats=holder.stats;

        details.setText(dataSet.get(listPosition).getDetails());

        if(setVisibility)
            stats.setText("Days: " + dataSet.get(listPosition).getStats());

        attendanceView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (setVisibility)
                { Intent i = new Intent(mcontext, AttendanceView.class);
                String userid=dataSet.get(listPosition).getUserid();
                i.putExtra("userid",userid);
                mcontext.startActivity(i);}

                else{


                    final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse() {

                        @Override
                        public void processFinish(String output, int response) {
                            if (response == 200 ) {
                                final FragmentTransaction ft = mfragment.beginTransaction();
                                ft.detach(frag);
                                ft.attach(frag);
                                ft.commit();

                                Toast.makeText(mcontext, "Approved",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(mcontext, "Kindly check your Internet connection",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    String userid=dataSet.get(listPosition).getUserid();
                    sendpost.paras.put("userid",userid);
                    sendpost.endpoint = "/attendance/approve";
                    sendpost.method = "POST";
                    sendpost.mycontext = mcontext;
                    sendpost.execute();

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

class DataModelJunior {

    String name;
    String position;
    String contact;
    String userid;
    String stats;

    public DataModelJunior(String name, String position, String contact, String userid, String stats) {
        this.name = name;
        this.position = position;
        this.contact = contact;
        this.userid=userid;
        this.stats=stats;
    }

    public DataModelJunior(String name, String position, String contact, String userid) {
        this.name = name;
        this.position = position;
        this.contact = contact;
        this.userid=userid;
    }

    public String getUserid() {
    return userid;
    }

    public String getStats() {
        return stats;
    }

    public String getDetails() {
        return "Name: "+ name + "\n" + "Position: "+ position + "\n" + "Contact: " + contact;
    }



}