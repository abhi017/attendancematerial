package com.sourcey.MyAttendence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String Token="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Token = sharedpreferences.getString("Token","");


        if (Token.isEmpty()){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse(){

                @Override
                public void processFinish(String output, int response){
                    if (response==200 && !(output.isEmpty())) {
                        try {

                            JSONObject obj = new JSONObject(output);
                            String UserDetails=obj.toString();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("UserDetails",UserDetails);
                            editor.commit();
                            Toast.makeText(MainActivity.this, "Login Success",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            Intent i = new Intent(MainActivity.this, HomePage.class);
                            startActivity(i);
                        }
                        catch (Throwable t) {
                            Toast.makeText(MainActivity.this, "Login failed",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Kindly check your Internet connection",
                                Toast.LENGTH_LONG).show();
                        finish();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                    }

                }
            });

            sendpost.paras.put("token",Token);
            sendpost.endpoint="/attendance/validateToken";
            sendpost.method = "POST";
            sendpost.mycontext=getApplicationContext();
            sendpost.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
