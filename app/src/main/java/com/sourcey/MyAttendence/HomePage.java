package com.sourcey.MyAttendence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.myprofile)Button profile;
    @BindView(R.id.markAt)Button mark;
    @BindView(R.id.myat)Button my;
    @BindView(R.id.juniorAt)Button junior;

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

    }
}
