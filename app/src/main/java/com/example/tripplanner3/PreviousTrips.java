package com.example.tripplanner3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class PreviousTrips extends AppCompatActivity {
    RecyclerView Prec;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Trip> PTrips = new ArrayList<Trip>();
    private RecyclerView.Adapter mAdapter;
    FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private int PERMISSION_ID=15;

    /////////////////////////////////////////////////////
    private GoogleSignInClient mGoogleSignInClient;

    //////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_trips);
        Intent EmailInt = getIntent();
        Prec = findViewById(R.id.RV2);
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        /////////-----------------------------------------------------------
        Prec.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(PreviousTrips.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PreviousTrips.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        Prec.setLayoutManager(layoutManager);
        //----------------------------------------------
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child(fuser.getUid());
        Query done = mDatabaseReference.orderByChild("status").equalTo("done");

        done.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                PTrips.clear();
                for(DataSnapshot dsn : dataSnapshot.getChildren())
                {
                    Trip oneTrip = dsn.getValue(Trip.class);

                    PTrips.add(oneTrip);
                }
                mAdapter = new PAdapter_class(PreviousTrips.this, PTrips);
                Prec.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //-------------------------------------------------------------------------------------------------------
    public class PAdapter_class extends RecyclerView.Adapter<PAdapter_class.ViewHolder>{

        Context context;
        AlertDialog dialog;
        AlertDialog.Builder builder;
        int Selected_item;
        int current_pos;

        ArrayList<Trip> values;

        @NonNull
        @Override
        public PAdapter_class.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View row = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.prev_row, parent, false);
            PAdapter_class.ViewHolder vh = new PAdapter_class.ViewHolder(row);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PAdapter_class.ViewHolder holder, final int position) {
            holder.Name.setText(values.get(position).getName());
            holder.StartPoint.setText("From"+values.get(position).getSp());
            holder.EndPoint.setText("To"+values.get(position).getEp());
            holder.DateTime.setText(values.get(position).getSd()+" At "+values.get(position).getSt());


            holder.pdelete.setOnClickListener(v -> {
                builder = new AlertDialog.Builder(context);
                builder.setMessage("ARE YOU SURE TO DELETE THIS Trip");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase DB = FirebaseDatabase.getInstance();
                        DatabaseReference mRDB = DB.getReference().child(fuser.getUid());
                        SQLAdapter sAdapter = new SQLAdapter();
                        sAdapter.deleteTrip(values.get(Selected_item).getId());

                        if (IsNetworkAvailable())
                        {
                            Query removed = mRDB.orderByChild("id").equalTo(values.get(Selected_item).getId());
                            removed.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dsn : dataSnapshot.getChildren()) {
                                        dsn.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        values.remove(Selected_item);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog=builder.create();
                dialog.show();
            });

        }
        @Override
        public int getItemCount() {
            return values.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView Name;
            public TextView StartPoint;
            public TextView EndPoint;
            public TextView DateTime;
            public Button pdelete;
            public ConstraintLayout contlayout;
            public View layout;

            public ViewHolder(View V) {
                super(V);
                layout = V;
                Name = V.findViewById(R.id.textView_pEN);
                StartPoint = V.findViewById(R.id.textView_pSP);
                EndPoint = V.findViewById(R.id.textView_pEP);
                DateTime = V.findViewById(R.id.textView_pDT);
                contlayout = V.findViewById(R.id.prow);
                pdelete = V.findViewById(R.id.button_pOptions);
            }
        }

        public PAdapter_class(Context context, ArrayList<Trip> values) {

            this.context = context;
            this.values = values;
        }
    }
    public boolean IsNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }
}

