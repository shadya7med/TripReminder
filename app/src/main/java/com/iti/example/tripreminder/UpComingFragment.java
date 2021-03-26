package com.iti.example.tripreminder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class UpComingFragment extends Fragment {
    FloatingActionButton addNewTripBtn;
    Intent intent;
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

        //Buttons Declaration
        addNewTripBtn = view.findViewById(R.id.btn_add_upcomingFrg);
        intent = new Intent(getActivity(), AddNewTrip.class);

        //OnClick Action Buttons
        addNewTripBtn.setOnClickListener(v -> startActivity(intent));
    }
}