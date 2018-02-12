package com.sourcey.MyAttendence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 212634269 on 09-Feb-18.
 */

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(checkQueue);
    }

    private Runnable checkQueue = new Runnable() {
        String id = "";
        String endpoint = "";
        String image = "";
        String userid = "";
        String coordinates = "";
        public void run() {
            // Do something here

            if (check_internet()) {
                final SQLiteHelper sQLiteHelper = new SQLiteHelper(BackgroundService.this);
                Cursor start = sQLiteHelper.getAllRecords();

                if (start.getCount() > 0) {
                    for (int i = 0; i < start.getCount(); i++) {
                        start.moveToNext();
                         id = start.getString(0);
                         endpoint = start.getString(1);
                         image = start.getString(2);
                         userid = start.getString(3);
                         coordinates = start.getString(4);

                        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse(){

                            @Override
                            public void processFinish(String output, int response){
                                if (response==200 && output.equals("\"marked\"")) {
                                    String[] atr_arr={id};
                                    sQLiteHelper.deleteRecord(atr_arr);
                                }
                            }
                        });

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());

                        sendpost.paras.put("userid",userid);
                        sendpost.paras.put("coordinates",coordinates);
                        sendpost.paras.put("picture",image);
                        sendpost.paras.put("date",currentDateandTime);
                        sendpost.mycontext=getApplicationContext();
                        sendpost.endpoint=endpoint;
                        sendpost.method = "POST";

                        sendpost.execute();

                    }
                }

            }
            System.out.println("The Background Service is running");
            stopSelf();
        }
    };


    public boolean check_internet(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}
