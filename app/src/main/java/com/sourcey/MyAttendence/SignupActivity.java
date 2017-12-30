package com.sourcey.MyAttendence;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sourcey.MyAttendence.SendPostRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.BindView;

import static com.google.android.gms.analytics.internal.zzy.b;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    ArrayList<HashMap<String,String>> managers = new ArrayList<>();
    ArrayList<String> managersArray = new ArrayList<>();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.user_name) EditText _usernameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.dob) EditText _dobText;
    @BindView(R.id.position) EditText _positionText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindView(R.id.manager) AutoCompleteTextView _manager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fetchUsers();

//        ListAdapter  adapterList = new SimpleAdapter(SignupActivity.this,managers,R.layout.autocomplete_dropdown,new String[]{"Username"},new int[R.id.manager]);




        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _usernameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    Toast.makeText(getApplicationContext(), "unfocus", Toast.LENGTH_LONG).show();
            }
        });

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    _dobText.setText(current);
                    _dobText.setSelection(sel < current.length() ? sel : current.length());
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        _dobText.addTextChangedListener(tw);
    }

    public void fetchUsers(){
        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse(){

            @Override
            public void processFinish(String output, int response){
                if (response == 200) {
                    try {
                        JSONArray obj = new JSONArray(output);
                        Log.d("My App", obj.toString());
                        for (int i=0; i<obj.length(); i++)
                        {
                            JSONObject manage = obj.getJSONObject(i);
                            String Id= manage.getString("Id");
                            String Username= manage.getString("Username");
                            String Password= manage.getString("Password");
                            String PasswordHash= manage.getString("PasswordHash");
                            String Token= manage.getString("Token");
                            String Name= manage.getString("Name");
                            String Dob= manage.getString("Dob");
                            String Position= manage.getString("Position");
                            String Email= manage.getString("Email");
                            String Contact= manage.getString("Contact");
                            String Created= manage.getString("Created");
                            String Seniorid= manage.getString("Seniorid");
                            String Approval= manage.getString("Approval");
                            HashMap<String,String> managetemp=new HashMap<>();
                            managetemp.put("Id",Id);
                            managetemp.put("Username",Username);
                            managetemp.put("Password",Password);
                            managetemp.put("PasswordHash",PasswordHash);
                            managetemp.put("Token",Token);
                            managetemp.put("Name",Name);
                            managetemp.put("Dob",Dob);
                            managetemp.put("Position",Position);
                            managetemp.put("Email",Email);
                            managetemp.put("Contact",Contact);
                            managetemp.put("Created",Created);
                            managetemp.put("Seniorid",Seniorid);
                            managetemp.put("Approval",Approval);
                            managersArray.add(Username);
                            managers.add(managetemp);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getApplicationContext(),R.layout.autocomplete_dropdown,managersArray);

                        _manager.setAdapter(adapter);
                        _manager.setThreshold(1);
                    }

                    catch (Throwable t) {
                        Log.e("My App", t.getMessage());
                    }
                   // onSignupSuccess();
                } else {
                    Toast.makeText(SignupActivity.this, "Kindly check your Internet connection",
                            Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(i);
                    //onSignupFailed();
                }

            }
        });
        sendpost.mycontext=getApplicationContext();
        sendpost.endpoint="/attendance/getUsers";
        sendpost.method="POST";
        sendpost.execute();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String dob = _dobText.getText().toString();
        String position = _positionText.getText().toString();
        String managerVal = _manager.getText().toString();
        String seniorId = "";
        final String Response;
        // TODO: Implement your own signup logic here.

        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse(){

            @Override
            public void processFinish(String output, int response){
                if (response==200 && !(output.isEmpty())) {
                    try {
                        JSONObject obj = new JSONObject(output);
                        Log.d("My App", obj.toString());
                        String Token=obj.getString("Token");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Token",Token);
                        editor.commit();
                        onSignupSuccess();
                    }
                    catch (Throwable t) {
                        onSignupFailed();
                        Log.e("My App", t.getMessage());
                    }


                } else {
                    onSignupFailed();
                }

            }
        });


        for(int i=0;i<managers.size();i++)
        {
            if(managers.get(i).get("Username").toString().equals(managerVal)){
                seniorId = managers.get(i).get("Id").toString();
            }
        }
        sendpost.paras.put("name",name);
        sendpost.paras.put("email",email);
        sendpost.paras.put("contact",mobile);
        sendpost.paras.put("password",password);
        sendpost.paras.put("username",username);
        sendpost.paras.put("dob",dob);
        sendpost.paras.put("position",position);
        sendpost.paras.put("seniorid",seniorId);
        sendpost.mycontext=getApplicationContext();
        sendpost.endpoint="/attendance/register";
        sendpost.method="POST";

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        sendpost.execute();
                        progressDialog.dismiss();
                        System.out.print("Response Code =" + sendpost.responseCode);
                    }
                }, 2000);
    }



    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
        finish();
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty()) {
            _usernameText.setError("Enter Valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}

