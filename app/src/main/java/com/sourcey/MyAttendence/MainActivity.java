package com.sourcey.MyAttendence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private Context context;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String Token="";
    String First="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);


        this.context = this;
        Intent alarm = new Intent(this.context, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Token = sharedpreferences.getString("Token","");


        if (Token.isEmpty()){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            First = sharedpreferences.getString("First", "");
            if (First.isEmpty() || First.equals("1")) {
                final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse() {

                    @Override
                    public void processFinish(String output, int response) {
                        if (response == 200 && !(output.isEmpty())) {
                            try {

                                JSONObject obj = new JSONObject(output);
                                String UserDetails = obj.toString();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("UserDetails", UserDetails);
                                editor.putString("First", "0");
                                editor.commit();
                                Toast.makeText(MainActivity.this, "Login Success",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                Intent i = new Intent(MainActivity.this, HomePage.class);
                                startActivity(i);
                            } catch (Throwable t) {
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

                sendpost.paras.put("token", Token);
                sendpost.endpoint = "/attendance/validateToken";
                sendpost.method = "POST";
                sendpost.mycontext = getApplicationContext();
                sendpost.execute();
            }

            else{
                finish();
                Intent i = new Intent(MainActivity.this, HomePage.class);
                startActivity(i);
            }
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
