package com.sourcey.MyAttendence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Abhinav Garg on 25-12-2017.
 */

public class SendPostRequest extends AsyncTask<String, Void, String> {

    String name;
    String address;
    String email;
    String mobile;
    String password;

    HashMap<String,String> paras = new HashMap<String,String>();
    String endpoint;
    String method;

    Context mycontext;
    int responseCode;

    public SendPostRequest() {

    }


    public interface AsyncResponse {
        void processFinish(String output, int response);
    }
    public AsyncResponse delegate = null;

    public SendPostRequest(AsyncResponse delegate){
        this.delegate = delegate;
    }
    public void onPreExecute(){}

    public String doInBackground(String... arg0) {

        try {
            String urlStr= "http://45.63.101.72" + endpoint;
            URL url = new URL(urlStr); // here is your URL path


                JSONObject postDataParams = new JSONObject();
            if(method.equals("POST")) {
                for (Map.Entry m : paras.entrySet()) {
                    System.out.println(m.getKey() + " " + m.getValue());
                    postDataParams.put(m.getKey().toString(), m.getValue().toString());
                }

                Log.e("params", postDataParams.toString());
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);

            if(method.equals("POST")) {
                conn.setRequestProperty("Content-Type",
                        "application/json;charset=utf-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //open
                conn.connect();

                //setup send
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(postDataParams.toString().getBytes());
                //clean up
                os.flush();

               /* OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();*/

               /* DataOutputStream  printout = new DataOutputStream(conn.getOutputStream ());
                printout.writeBytes(URLEncoder.encode(postDataParams.toString(),"UTF-8"));
                printout.flush ();
                printout.close ();*/
            }

             responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";
                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    public void onPostExecute(String result) {
        Toast.makeText(mycontext, result,
                Toast.LENGTH_LONG).show();
        delegate.processFinish(result,responseCode);
    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}