package com.iti.example.tripreminder.Fragments;

import android.content.Context;
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

import com.iti.example.tripreminder.Activities.PastTripsActivity;
import com.iti.example.tripreminder.Adapters.TripsListAdapter;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;
    TripsListAdapter tripsListAdapter;
    ArrayList<Trips> tripsList;
    Handler notesListUpdater ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycle_history);

        //get All upcoming from Room
        AppDatabase db = TripReminderDatabase.getInstance(getContext()).getAppDatabase();
        new Thread(){
            @Override
            public void run() {
                tripsList.clear();
                tripsList.addAll(db.tripDao().getTripsByType("History"));
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
        tripsListAdapter = new TripsListAdapter(getContext(),tripsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(tripsListAdapter);
    }
}