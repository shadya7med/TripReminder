package com.iti.example.tripreminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<String> data;

    Adapter (Context context,List<String> data){
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.card_view,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
          String title = data.get(i);
          viewHolder.TripName.setText(title);
    }

    @Override
    public int getItemCount() {return data.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        Intent edit_trip_intent,start_trip_intent;
        ImageView delete,edit,start;
        TextView TripName,StartingPoint,Destination,TripDate,TripTime,Notes;
        public ViewHolder(@NonNull View v) {
            super(v);
            TripName = v.findViewById(R.id.txt_tripName_cardview);
            StartingPoint = v.findViewById(R.id.txt_startingTime_cardview);
            Destination = v.findViewById(R.id.txt_destination_cardview);
            TripDate = v.findViewById(R.id.txt_date_cardview);
            TripTime = v.findViewById(R.id.txt_startingTime_cardview);
            Notes = v.findViewById(R.id.txt_note_cardview);
            edit = v.findViewById(R.id.edit_img);
            start = v.findViewById(R.id.start_img);
            delete = v.findViewById(R.id.delete_img);
            //Intents
           // edit_trip_intent = new Intent(getActivity(),EditTrip.class);
            //start_trip_intent = new Intent(getActivity(),MapFragment.class);
            //OnClick Action Buttons
           // edit.setOnClickListener(v -> startActivity(edit_trip_intent));
            //start.setOnClickListener(v -> startActivity(start_trip_intent));
        }
    }
}
