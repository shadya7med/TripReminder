package com.iti.example.tripreminder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class UpComingFragment extends Fragment {
    FloatingActionButton addNewTripBtn;
    Intent new_trip_intent;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> items;
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
        //Views Declaration
        addNewTripBtn = view.findViewById(R.id.btn_add_upcomingFrg);
        recyclerView = view.findViewById(R.id.recyclerview_upcomingFrag);
        items = new ArrayList<>();
        adapter = new Adapter(this.getContext(),items);
        new_trip_intent = new Intent(getActivity(), AddNewTrip.class);
        //Actions
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        items.add("Trip-One");
        items.add("Trip-Two");
        addNewTripBtn.setOnClickListener(v -> startActivity(new_trip_intent));

    }
}