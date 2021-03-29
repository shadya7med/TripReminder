package com.iti.example.tripreminder.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Adapters.TripsListAdapter;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;

import java.util.ArrayList;


public class UpComingFragment extends Fragment {

    public static final int ADD_NEW_TRIP_REQ_CODE = 50;
    public static final String TRIP_INFO = "trip_info";

    FloatingActionButton addNewTripBtn;

    RecyclerView recyclerView;
    TripsListAdapter tripsListAdapter;
    ArrayList<Trips> tripsList;
    Handler notesListUpdater ;

    Context context;

    public UpComingFragment(Context _context){
        context = _context ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        /*refer to views*/
        addNewTripBtn = view.findViewById(R.id.btn_add_upcomingFrg);
        recyclerView = view.findViewById(R.id.recyclerview_upcomingFrag);

        //get All upcoming from Room
        AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
        new Thread(){
            @Override
            public void run() {
                tripsList.clear();
                tripsList.addAll(db.tripDao().getAllTrips());
                notesListUpdater.handleMessage(new Message());
            }
        }.start();
        tripsList = new ArrayList<>();
        notesListUpdater = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                tripsListAdapter.notifyDataSetChanged();

            }
        };

        //recyclerView configuration
        tripsListAdapter = new TripsListAdapter(context,tripsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(tripsListAdapter);


        Intent addNewTripIntent = new Intent(context, AddNewTripActivity.class);
        addNewTripBtn.setOnClickListener(v -> startActivityForResult(addNewTripIntent,ADD_NEW_TRIP_REQ_CODE));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent tripDataIntent) {
        if(requestCode == ADD_NEW_TRIP_REQ_CODE){
            if(resultCode == Activity.RESULT_OK){
                Trips trip = (Trips) tripDataIntent.getSerializableExtra(TRIP_INFO);
                //add Trip to Room
                /*get database instance*/
                AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                /*add trip data to Room*/
                new Thread(){
                    @Override
                    public void run() {
                        //trip inserted into db
                        db.tripDao().insertOne(trip);
                        //trip added to local trips array list
                        tripsList.add(trip);
                        notesListUpdater.sendMessage(new Message());
                    }
                }.start();

            }
        }
    }
}