package com.iti.example.tripreminder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;

import java.util.ArrayList;

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Trips> tripsList;
    private int duration;
    private Context context;

    public TripsListAdapter(Context context, ArrayList<Trips> tripsList){
        this.context = context ;
        this.layoutInflater = LayoutInflater.from(context);
        this.tripsList = tripsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.card_view,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
          duration = 10 ;
          String title = tripsList.get(i).tripName;
          viewHolder.TripName.setText(title);
          viewHolder.start.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                    //stop work scheduled for current trip
                  WorkManager.getInstance(context).cancelAllWorkByTag(title);
              }
          });

    }

    @Override
    public int getItemCount() {return tripsList.size();}

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
           // edit_trip_intent = new Intent(getActivity(),EditTripActivity.class);
            //start_trip_intent = new Intent(getActivity(),MapFragment.class);
            //OnClick Action Buttons
           // edit.setOnClickListener(v -> startActivity(edit_trip_intent));
            //start.setOnClickListener(v -> startActivity(start_trip_intent));
        }
    }
}
