package com.sourcey.MyAttendence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfile extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @BindView(R.id.name)TextView name;
    @BindView(R.id.userNameVal)TextView userName;
    @BindView(R.id.emailVal)TextView email;
    @BindView(R.id.dobVal)TextView dob;
    @BindView(R.id.positionVal)TextView position;
    @BindView(R.id.contactVal)TextView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String details = sharedpreferences.getString("UserDetails","");
        JSONObject obj=new JSONObject();
        String userid="";
        try{
            obj = new JSONObject(details);
            name.setText( obj.getString("Name"));
            userName.setText( obj.getString("Username"));
            email.setText( obj.getString("Email"));
            dob.setText( obj.getString("Dob"));
            position.setText( obj.getString("Position"));
            contact.setText( obj.getString("Contact"));
        }
        catch(Throwable t)
        {
            Log.e("App: ", "Failed to get id");
        }
    }
}
