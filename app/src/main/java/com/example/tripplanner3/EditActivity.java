package com.example.tripplanner3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Trip editing = new Trip();
    EditText trip_name;
    AutoCompleteTextView strt_pint;
    AutoCompleteTextView end_pint;
    TextView date;
    TextView time;
    Button chng_d,chng_t,Save_changs;
    private int mYear,mMonth,mDay,mHour,mMin;
    String trip_nm, spoint="", sLat="", sLong="", epoint="", eLat="", eLong="", status, sdate,totaltime,rdate,rtime;
    double mysLat, mysLong, myeLat, myeLong;
    int hours,min;
    int num;
    Date date1;
    Date date2;
    Date myTime;
    Date myDateCheck;
    int returnhour;
    int returnmin;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("EDITING");
        Intent getint = new Intent();
        getint = getIntent();
        editing = (Trip) getint.getSerializableExtra("Edited_trip");


        trip_name = findViewById(R.id.tripname_edit);
        strt_pint = findViewById(R.id.SP_edit);
        strt_pint.setAdapter(new PlaceAutoSuggestAdapter(EditActivity.this,android.R.layout.simple_list_item_1));

        end_pint = findViewById(R.id.EP_edit);
        strt_pint.setAdapter(new PlaceAutoSuggestAdapter(EditActivity.this,android.R.layout.simple_list_item_1));


        date = findViewById(R.id.date_edit);
        time = findViewById(R.id.time_edit);
        chng_d = findViewById(R.id.Change_date);
        chng_t = findViewById(R.id.Change_time);
        Save_changs = findViewById(R.id.save_changes);

        trip_name.setText(editing.getName());
        strt_pint.setText(editing.getSp());
        end_pint.setText(editing.getEp());
        date.setText("DATE : " + editing.getSd());
        time.setText("Time : " + editing.getSt());

        strt_pint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("jghhg");
                Log.d("Address : ",strt_pint.getText().toString());
                LatLng latLng=getSLatLngFromAddress(strt_pint.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);
                    if(address!=null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                        spoint = strt_pint.getText().toString();
                        mysLat = latLng.latitude;
                        mysLong = latLng.longitude;
                        sLat = mysLat + "";
                        sLong = mysLong + "";
                        System.out.println(spoint+sLat+sLong);

                    }
                    else {
                        Log.d("Adddress","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });

        end_pint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ",end_pint.getText().toString());
                LatLng latLng=getELatLngFromAddress(end_pint.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);
                    if(address!=null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                        epoint = end_pint.getText().toString();
                        myeLat = latLng.latitude;
                        myeLong = latLng.longitude;
                        eLat = myeLat + "";
                        eLong = myeLong + "";
                        System.out.println(epoint+eLat+eLong);
                    }
                    else {
                        Log.d("Adddress","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });

       chng_d.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showDatePickerDialog();
           }

       });

       chng_t.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showTimePickerDialog();
           }
       });




    }
    private void showTimePickerDialog()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),false
        );
        timePickerDialog.show();
    }
    private void showDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }


    private LatLng getSLatLngFromAddress(String address) {

        Geocoder geocoder = new Geocoder(EditActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                spoint = strt_pint.getText().toString();
                mysLat = latLng.latitude;
                mysLong = latLng.longitude;
                sLat = mysLat + "";
                sLong = mysLong + "";
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private LatLng getELatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(EditActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                epoint = end_pint.getText().toString();
                myeLat = latLng.latitude;
                myeLong = latLng.longitude;
                eLat = myeLat + "";
                eLong = myeLong + "";

                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(EditActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String myDate = dayOfMonth + "-" + (month + 1) + "-" + year;
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            date1 = format.parse(timeStamp);
            myDateCheck = format.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(myDateCheck.equals(null)) {
            try {
                myDateCheck = format.parse(timeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            if (myDateCheck.before(date1)) {
                Toast.makeText(view.getContext(), "Enter Valid Date", Toast.LENGTH_SHORT).show();
            } else {
                //txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                date.setText("DATE : "+dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar myCalInstance = Calendar.getInstance();
        Calendar myRealCalender = Calendar.getInstance();
        if(myDateCheck==null){
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            try {
                myDateCheck = format.parse(timeStamp);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        myRealCalender.setTime(myDateCheck);
        myRealCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
        myRealCalender.set(Calendar.MINUTE,minute);
        if((myRealCalender.getTime()).before(myCalInstance.getTime()))
        {
            Toast.makeText(view.getContext(),"Enter Valid Time",Toast.LENGTH_SHORT).show();
        }
        else {
            hours = hourOfDay;
            min = minute;
            if(hourOfDay<10&&min>=10) {
                time.setText("TIME : "+"0" + hourOfDay + ":" + minute);
            }
            if(hourOfDay<10&&min<10)
            {
                time.setText("TIME : "+"0" + hourOfDay + ":"+"0"+ minute);
            }
            if(hourOfDay>=10&&min<10)
            {
                time.setText("TIME : "+hourOfDay + ":"+"0"+ minute);
            }
            if(hourOfDay>=10&&min>=10)
            {
                time.setText("TIME : "+hourOfDay + ":"+ minute);

            }

        }
    }
}
