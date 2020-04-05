package com.example.tripplanner3;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
// HomeActivity
public class HomeActivity extends AppCompatActivity implements Changes_SQL {
    RecyclerView rec;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Trip> MyEvents = new ArrayList<Trip>();
    private RecyclerView.Adapter mAdapter;
    Button addtripbtn,Logoutbtn;
    String Email;
    FirebaseUser fuser;
    Button ShowPast;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private int tripPosition;
    /////////////////////////////////////////////////////
    private GoogleSignInClient mGoogleSignInClient;
    private int PERMISSION_ID=15;

    //////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent EmailInt = getIntent();
        Email = EmailInt.getStringExtra("EMAIL");
        rec = findViewById(R.id.RV1);
        addtripbtn = findViewById(R.id.Add_trip);
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        ShowPast = findViewById(R.id.past);
        ShowPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTopasttripActivity = new Intent(HomeActivity.this, PreviousTrips.class);
                startActivity(goTopasttripActivity);
            }
        });


        //fuser.getUid();
        /////////-----------------------------------------------------------
        Logoutbtn= findViewById(R.id.logoutbtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(HomeActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //updateUI(null);
                            }
                        });
                finish();


            }
        });

        addtripbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToAddtripActivity = new Intent(HomeActivity.this, AddTripActivity.class);
                goToAddtripActivity.putExtra(Keys.KEY_EMAIL, "Hello " + Email);
                startActivity(goToAddtripActivity);
            }
        });

//        if(IsNetworkAvailable())
//        {
//            SynchroizingTrips ST = new SynchroizingTrips();
//        }

        rec.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rec.setLayoutManager(layoutManager);
       // DataChanged_upcoming();
        //----------------------------------------------

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child(fuser.getUid());
        Query upcoming = mDatabaseReference.orderByChild("status").equalTo("upcoming");

        upcoming.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                MyEvents.clear();

                for(DataSnapshot dsn : dataSnapshot.getChildren())
                {
                    Trip oneTrip = dsn.getValue(Trip.class);

                    MyEvents.add(oneTrip);

                }
                mAdapter = new Adapter_class(HomeActivity.this, MyEvents);
                rec.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void DataChanged_upcoming()
    {
        SQLAdapter sqlAdpt = new SQLAdapter();
        MyEvents = sqlAdpt.StatusTrips(fuser.getEmail(),"upcoming");

        mAdapter = new Adapter_class(HomeActivity.this, MyEvents);
        rec.setAdapter(mAdapter);
    }

    @Override
    public void DataChanged_previous() {

    }


//////----------------------------------------------------------------------------------------------------/////////////////

    public class Adapter_class extends RecyclerView.Adapter<Adapter_class.ViewHolder> implements PopupMenu.OnMenuItemClickListener{

        Context context;
        AlertDialog dialog;
        AlertDialog.Builder builder;
        int Selected_item;
        Changes_SQL csql;

        ArrayList<Trip> values;

        @NonNull
        @Override
        public Adapter_class.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //TextView Name =
            View row = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.one_event, parent, false);
            Adapter_class.ViewHolder vh = new Adapter_class.ViewHolder(row);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter_class.ViewHolder holder, final int position) {
            holder.Name.setText(values.get(position).getName());
            holder.StartPoint.setText(values.get(position).getSp());
            holder.EndPoint.setText(values.get(position).getEp());
            holder.DateTime.setText(values.get(position).getSd()+" "+values.get(position).getSt());
            Selected_item = position;
            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showpopup(v,position);
                }
            });
            holder.Start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String latitude = values.get(position).getElat();
                    String longitude = values.get(position).getElong();
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
                            Toast.makeText(HomeActivity.this, "Turn on location", LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        //Request permission
                        ActivityCompat.requestPermissions(HomeActivity.this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);

                    }
                    Query done = mFirebaseDatabase.getReference().child(fuser.getUid()).orderByChild("id").equalTo(values.get(Selected_item).getId());
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

        }
        public void showpopup(View v,int Pos)
        {
            Selected_item=Pos;
            PopupMenu popup = new PopupMenu(HomeActivity.this,v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.pop_menu);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch(item.getItemId())
            {
                case R.id.I1:
                    //this is View
                    Intent goToViewtripActivity = new Intent(HomeActivity.this, ViewActivity.class);
                    goToViewtripActivity.putExtra("SelectedTrip", values.get(Selected_item));
                    startActivity(goToViewtripActivity);

                    return true;

                case R.id.I2:
                    //this is Add Notes
                    // Intent to put tripId to the note
                    Intent intent2 = new Intent(context, AddNoteActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra("trip_id", MyEvents.get(tripPosition).getId());
                    context.startActivity(intent2);
                    return true;

                case R.id.I4:
                    //this is Delete
                    builder = new AlertDialog.Builder(context);
                    builder.setMessage("ARE YOU SURE TO DELETE THIS EVENT");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase DB = FirebaseDatabase.getInstance();
                            DatabaseReference mRDB = DB.getReference().child(fuser.getUid());
                            SQLAdapter sAdapter = new SQLAdapter();
                            sAdapter.deleteTrip(values.get(Selected_item).getId());
                            values.remove(Selected_item);
                            //csql.DataChanged_upcoming();

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


                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dialog=builder.create();
                    dialog.show();
                    return true;
                default : return false;

            }
        }

        @Override
        public int getItemCount()
        {
            return values.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView Name;
            public TextView StartPoint;
            public TextView EndPoint;
            public TextView DateTime;
            public Button options;
            public Button Start;
            public ConstraintLayout contlayout;
            public View layout;

            public ViewHolder(View V) {
                super(V);
                layout = V;
                Name = V.findViewById(R.id.textView_EN);
                StartPoint = V.findViewById(R.id.textView_SP);
                EndPoint = V.findViewById(R.id.textView_EP);
                DateTime = V.findViewById(R.id.textView_DT);
                contlayout = V.findViewById(R.id.row);
                options = V.findViewById(R.id.button_Options);
                Start = V.findViewById(R.id.button_Start);
            }
        }

        public Adapter_class(Context context, ArrayList<Trip> values) {

            this.context = context;
            this.values = values;
        }
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
    private void getLastLocation(){
        if(checkPermissions())
        {
            if(isLocationEnabled())
            {

            } else {
                Toast.makeText(this, "Turn on location", LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            //Request permission
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);

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
