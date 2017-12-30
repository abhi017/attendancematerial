package com.sourcey.MyAttendence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateAttendence extends AppCompatActivity {


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    String cood = "";
    double latitude = 0;
    double longitude = 0;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_attendence);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        final SendPostRequest sendpost = new SendPostRequest(new SendPostRequest.AsyncResponse() {

            @Override
            public void processFinish(String output, int response) {
                if (response == 200 && !(output.isEmpty())) {
                    try {

                        JSONArray obj = new JSONArray(output);
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject attendance = obj.getJSONObject(i);
                            cood = attendance.getString("Coordinates");
                            String[] cood_spl = cood.split(" ");
                            latitude = Double.parseDouble(cood_spl[0]);
                            longitude = Double.parseDouble(cood_spl[1]);
                            String address_got = getAddress();
                            String Pic = attendance.getString("Picture");
                            data.add(new DataModel(
                                    address_got,
                                    cood,
                                    Pic
                            ));

                        }

                        adapter = new CustomAdapter(data,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    } catch (Throwable t) {
                        Toast.makeText(DateAttendence.this, "Kindly check your Internet connection",
                                Toast.LENGTH_LONG).show();
                        finish();
                        Intent i = new Intent(DateAttendence.this, AttendanceView.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(DateAttendence.this, "Kindly check your Internet connection",
                            Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(DateAttendence.this, AttendanceView.class);
                    startActivity(i);
                }

            }
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String details = sharedpreferences.getString("UserDetails", "");
        JSONObject obj = new JSONObject();
        String userid = "";
        try {
            obj = new JSONObject(details);
            userid = obj.getString("Id");
        } catch (Throwable t) {
            Log.e("App: ", "Failed to get id");
        }

        Intent intent = getIntent();
        String date= intent.getStringExtra("Date");

        sendpost.paras.put("userid", userid);
        sendpost.paras.put("date", date);
        sendpost.endpoint = "/attendance/getDetails";
        sendpost.method = "POST";
        sendpost.mycontext = getApplicationContext();
        sendpost.execute();


    }


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public String getAddress() {

        Address locationAddress = getAddress(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;

                return currentLocation;

            }

        }
        return "";
    }
}

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<DataModel> dataSet;
        Context mcontext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView address;
            ImageView userImage;
            RelativeLayout onMap;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.address = (TextView) itemView.findViewById(R.id.tvAddress);
                this.userImage = (ImageView) itemView.findViewById(R.id.userImage);
                this.onMap = (RelativeLayout) itemView.findViewById(R.id.rlPickLocation);
            }
        }

        public CustomAdapter(ArrayList<DataModel> data, Context mcontext) {
            this.dataSet = data;
            this.mcontext = mcontext;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_row, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView address = holder.address;
            ImageView userImage = holder.userImage;
            RelativeLayout onMap = holder.onMap;

            address.setText(dataSet.get(listPosition).getAddress());
            String encodedImage = dataSet.get(listPosition).getImage();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userImage.setImageBitmap(decodedByte);

            onMap.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    double latitude = Double.parseDouble(dataSet.get(listPosition).getLat());
                    double longitude = Double.parseDouble(dataSet.get(listPosition).getLongi());

                    String uri = String.format(Locale.ENGLISH, "geo:<%f>,<%f>?q=<%f>,<%f>", latitude, longitude,latitude, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    class DataModel {

        String address;
        String coordinates;
        String Lat;
        String Longi;
        String image;

        public DataModel(String address, String coordinates, String image) {
            this.address = address;
            this.coordinates = coordinates;
            this.image = image;
        }

        public String getAddress() {
            return address;
        }

        public String getLat() {
            String[] cood = coordinates.split(" ");
            return cood[0];
        }

        public String getLongi() {
            String[] cood = coordinates.split(" ");
            return cood[1];
        }

        public String getImage() {
            return image;
        }


    }
