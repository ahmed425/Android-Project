package com.example.tripplanner3;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.content.SharedPreferences;


import java.util.ArrayList;

public class SynchroizingTrips {
    FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase DB;
    private DatabaseReference mRDB;
    ArrayList<Trip> MyTrips;
    Query Pushed;
    Query query_nexist;
    int i;
    int ex;
    ArrayList<Trip> NotExisting;
    public static final String saveData="NewData";
    String user;
    SharedPreferences saving;

    public SynchroizingTrips()
    {

//        saving = getSharedPreferences(saveData,0);
//        user = saving.getString("user", "null");
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        SQLAdapter sqlAdapter =new SQLAdapter();
        MyTrips = sqlAdapter.retrieveTrips(fuser.getEmail());

        //Case 1 : push all updated and added trips from sqlite to firebase
        for(i=0;i<MyTrips.size();i++)
        {
             DB = FirebaseDatabase.getInstance();
             mRDB = DB.getReference().child(fuser.getUid());

             Pushed=null;
             Pushed = mRDB.orderByChild("id").equalTo(MyTrips.get(i).getId());
            if(Pushed != null)
            {
                Pushed.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsn : dataSnapshot.getChildren())
                        {
                            if(!(dsn.equals(MyTrips.get(i))))
                            {
                                dsn.getRef().setValue(MyTrips.get(i));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else
            {
                String key = mRDB.push().getKey();
                mRDB.child(key).setValue(MyTrips.get(i));
            }
        }


        //Case 2 : delete all trips that are not exists in sqlite from firebase

        mRDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                NotExisting.clear();

                for(DataSnapshot dsn : dataSnapshot.getChildren())
                {
                    Trip oneTrip = dsn.getValue(Trip.class);
                    NotExisting.add(oneTrip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for(i=0;i<NotExisting.size();i++)
        {
            if(!MyTrips.contains(NotExisting.get(i)))
            {
                query_nexist = mRDB.orderByChild("id").equalTo(NotExisting.get(i).getId());
                query_nexist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot dsn : dataSnapshot.getChildren()) {
                            dsn.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }


    }
}
