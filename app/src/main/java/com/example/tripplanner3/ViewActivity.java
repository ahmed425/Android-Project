package com.example.tripplanner3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class ViewActivity extends AppCompatActivity {
    TextView NV;
    TextView SV;
    TextView EV;
    TextView DTV;
    TextView SSV;
    ListView Nots;
    Button start_v;
    Button Edit_v;
    FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private int PERMISSION_ID=15;
    Trip selected = new Trip();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent getint = new Intent();
        getint = getIntent();
        selected = (Trip) getint.getSerializableExtra("SelectedTrip");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();

        NV = findViewById(R.id.NV);
        SV = findViewById(R.id.SV);
        EV = findViewById(R.id.EV);
        DTV = findViewById(R.id.DTV);
        SSV = findViewById(R.id.SSV);
        Nots = findViewById(R.id.List1);
        start_v = findViewById(R.id.start_view);
        Edit_v = findViewById(R.id.edit_view);

        NV.setText(selected.getName());
        SV.setText(selected.getSp());
        EV.setText(selected.getEp());
        DTV.setText(selected.getSd() +" AT " +selected.getSt());
         ArrayList<String> notes = new ArrayList<String>();
         notes = selected.getNotes();
         if(notes == null)
         {
             notes = new ArrayList<String>();
             notes.add("NO NOTES");
         }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,notes);
        Nots.setAdapter(adapter);
        start_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = selected.getElat();
                String longitude = selected.getElong();
                if(checkPermissions())
                {
                    if(isLocationEnabled())
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                        intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }

                    } else {
                        Toast.makeText(ViewActivity.this, "Turn on location", LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                    //Request permission
                    ActivityCompat.requestPermissions(ViewActivity.this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);

                }
                Query done = mFirebaseDatabase.getReference().child(fuser.getUid()).orderByChild("id").equalTo(selected.getId());
                done.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsn : dataSnapshot.getChildren()) {
                            dsn.getRef().child("status").setValue("done");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Edit_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent goToAddtripActivity = new Intent(ViewActivity.this, EditActivity.class);
                goToAddtripActivity.putExtra("Edited_trip", selected);
                startActivity(goToAddtripActivity);

            }
        });


    }
    private boolean checkPermissions()
    {
        boolean c = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED;
        boolean c2 = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;
        if(c && c2)
        {
            return  true;
        }
        return false;
    }

    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );

    }


}

