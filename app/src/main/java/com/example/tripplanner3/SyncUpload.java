package com.example.tripplanner3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class SyncUpload extends AsyncTask<Trip, Void, Integer> {


    Trip trip;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private FirebaseUser fuser;
    SQLAdapter adapter;
    Context context;

    public SyncUpload(Context context, FirebaseAuth mAuth, DatabaseReference db, FirebaseUser fuser, Trip trip) {
        this.mAuth = mAuth;
        this.db = db;
        this.fuser = fuser;
        this.context = context;
        this.trip = trip;
    }

    @Override
    protected Integer doInBackground(Trip... trips) {
        final SQLAdapter adapter = new SQLAdapter(context);
        String key = db.child(fuser.getUid()).push().getKey();
        trip.setEd(key);
        long i = adapter.updateTrip(trip.getId(), trip.getName(), trip.getSp(), trip.getSlong(),
                trip.getSlat(), trip.getEp(), trip.getElong(), trip.getElat(), trip.getStatus(),
                trip.getSd(), trip.getEd(), trip.getSt(), trip.getSt(), trip.getRep(), trip.getUser(), null);
        db.child(fuser.getUid()).child(key).setValue(trip);
       // Log.i("a5eryom", "awel mara " + i + trip.getEd());

        return null;
    }
}
