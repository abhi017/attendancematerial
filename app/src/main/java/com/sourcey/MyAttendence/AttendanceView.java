package com.sourcey.MyAttendence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceView extends AppCompatActivity {
    JSONArray obj1 = new JSONArray();
    List<EventDay> events = new ArrayList<>();
    String userid;
    @BindView(R.id.calendarView)CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_view);

        ButterKnife.bind(this);




        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse(){

            @Override
            public void processFinish(String output, int response){
                if (response==200 && !(output.isEmpty())) {
                    try {

                        JSONArray obj = new JSONArray(output);
                        obj1=obj;
                        for (int i=0; i<obj.length(); i++)
                        {
                            JSONObject attendance = obj.getJSONObject(i);
                            String date= attendance.getString("Date");
                            String[] dates1=date.split(" ");
                            String[] dates=dates1[0].split("-");

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.MONTH, Integer.parseInt(dates[1])-1);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));
                            calendar.set(Calendar.YEAR, Integer.parseInt(dates[0]));
                            events.add(new EventDay(calendar, R.drawable.present1));
                        }

                        calendarView.setEvents(events);
                    }
                    catch (Throwable t) {
                        Toast.makeText(AttendanceView.this, "No Attendance Marked",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AttendanceView.this, "Kindly check your Internet connection",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

       /* sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String details = sharedpreferences.getString("UserDetails","");
        JSONObject obj=new JSONObject();
        String userid="";
        try{
            obj = new JSONObject(details);
            userid = obj.getString("Id");
        }
        catch(Throwable t)
        {
            Log.e("App: ", "Failed to get id");
        }*/

        Intent intent = getIntent();
        userid= intent.getStringExtra("userid");

        sendpost.paras.put("userid",userid);
        sendpost.endpoint="/attendance/getAll";
        sendpost.method = "POST";
        sendpost.mycontext=getApplicationContext();
        sendpost.execute();





        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar test = eventDay.getCalendar();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String Date = dateFormat.format(test.getTime()).toString();

                Intent i = new Intent(getApplicationContext(), DateAttendence.class);
                i.putExtra("Date", Date);
                i.putExtra("userid", userid);
                startActivity(i);
            }
        });


    }
    }

