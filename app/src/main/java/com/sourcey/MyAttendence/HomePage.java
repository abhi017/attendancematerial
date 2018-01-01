package com.sourcey.MyAttendence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.myprofile)Button profile;
    @BindView(R.id.markAt)Button mark;
    @BindView(R.id.myat)Button my;
    @BindView(R.id.juniorAt)Button junior;
    @BindView(R.id.logout)Button logout;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(i);

            }
        });

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), FetchLocation.class);
                startActivity(i);

            }
        });

        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String details = sharedpreferences.getString("UserDetails","");
                JSONObject obj;
                String userid="";
                try{
                    obj = new JSONObject(details);
                    userid = obj.getString("Id");
                    Intent i = new Intent(getApplicationContext(), AttendanceView.class);
                    i.putExtra("userid",userid);
                    startActivity(i);
                }
                catch(Throwable t)
                {
                    Log.e("App: ", "Failed to get id");
                }


            }
        });

        junior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Juniors.class);
                startActivity(i);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Token","");
                editor.commit();
                finish();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

            }
        });
    }
}
