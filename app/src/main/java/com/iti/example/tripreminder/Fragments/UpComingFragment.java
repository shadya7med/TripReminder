package com.iti.example.tripreminder.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Adapters.TripsListAdapter;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;

import java.util.ArrayList;
import java.util.List;


public class UpComingFragment extends Fragment {

    public static final int ADD_NEW_TRIP_REQ_CODE = 50;
    public static final String TRIP_INFO = "trip_info";

    FloatingActionButton addNewTripBtn;

    RecyclerView recyclerView;
    TripsListAdapter tripsListAdapter;
    public ArrayList<Trips> tripsList;
    public Handler notesListUpdater;
    DatabaseReference tripsRef;
    Context context;
    String userId;

    public UpComingFragment(Context _context) {
        context = _context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("UpComing Trips");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_up_coming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*init local Trips List*/
        tripsList = new ArrayList<>();
        /*refer to views*/
        addNewTripBtn = view.findViewById(R.id.btn_add_upcomingFrg);
        recyclerView = view.findViewById(R.id.recyclerview_upcomingFrag);
        /*set Button Listener*/
        addNewTripBtn.setOnClickListener(v -> {
            // First it confirms whether the
            // 'Display over other apps' permission in given
            if (checkOverlayDisplayPermission()) {
                Intent addNewTripIntent = new Intent(context, AddNewTripActivity.class);
                startActivityForResult(addNewTripIntent, ADD_NEW_TRIP_REQ_CODE);
            } else {
                // If permission is not given,
                // it shows the AlertDialog box and
                // redirects to the Settings
                requestOverlayDisplayPermission();
            }

        });
        /*create Handler to update the UI*/
        notesListUpdater = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                tripsListAdapter.notifyDataSetChanged();

            }
        };
        //get Current UserId
        //this if should be always true --> check
        //if (FirebaseAuth.getInstance().getCurrentUser() != null) {} //redundant check
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get All upcoming from Room
        AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
        new Thread() {
            @Override
            public void run() {
                tripsList.clear();
                List<Trips> trips = db.tripDao().getTripsForUserByStatus(userId, AddNewTripActivity.TRIP_STATUS_UPCOMING);
                tripsList.addAll(trips);
                notesListUpdater.sendMessage(new Message());
            }
        }.start();


        /*connectedRef = FirebaseDatabase.getInstance().getReference("Trips");
       connectedRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tripsList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Trips trip = dataSnapshot.getValue(Trips.class);
                        tripsList.add(trip);
                    }

                  //  Toast.makeText(context, ""+tripsList.size(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/


        //recyclerView configuration
        tripsListAdapter = new TripsListAdapter(context, tripsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(tripsListAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent tripDataIntent) {
        if (requestCode == ADD_NEW_TRIP_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Trips trip = (Trips) tripDataIntent.getSerializableExtra(TRIP_INFO);
                //add Trip to Room
                /*get database instance*/
                AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                /*add trip data to Room*/
                new Thread() {
                    @Override
                    public void run() {
                        //trip inserted into db
                        // db.tripDao().insertOne(trip);
                        //trip added to local trips array list
                        tripsList.add(trip);
                        notesListUpdater.sendMessage(new Message());
                    }
                }.start();
            }
        }
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));

                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, Activity.RESULT_OK);
            }
        });
        AlertDialog dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(context)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
//u6kP1gRYqRVHMA0HjjLRVZRnQGw2