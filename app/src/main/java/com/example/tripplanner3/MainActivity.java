package com.example.tripplanner3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
Button addtripbtn,addNotebtn;
    ArrayList<Trip> trips;
    SQLAdapter adapter ;
    private FirebaseAuth mAuth;
//    private Context context;
    TextView txt;

    String user;

    @Override
    protected void onStart() {
        super.onStart();
        adapter=new SQLAdapter(getApplicationContext());
        user= getIntent().getStringExtra("username");
        txt=findViewById(R.id.txtv);


//        trips=adapter.retrieveTrips(user);
//        if(!trips.get(0).getName().isEmpty()) {
//            txt.setText(trips.get(0).getName());
//        }

    }

    //    private FirebaseUser fuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNotebtn=findViewById(R.id.addNotebtn);
        addtripbtn=findViewById(R.id.addTrip);
        addNotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(this, AddTripActivity.class));
                Intent intent2 = new Intent(MainActivity.this, AddNoteActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                adapter.retrieveTrips(user);
//                if(trips.get(0).getId()==1) {
//                    intent2.putExtra("trip_id", trips.get(0).getId());
//                }
                startActivity(intent2);
            }
        });

        addtripbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, AddTripActivity.class);
//                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent2.putExtra("trip_id", trips.get(0).getId());
                startActivity(intent3);
            }
        });

    }
}
