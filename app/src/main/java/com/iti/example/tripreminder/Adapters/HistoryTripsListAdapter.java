package com.iti.example.tripreminder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Activities.EditTripActivity;
import com.iti.example.tripreminder.Activities.HomeActivity;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Services.FloatingWidgetService;

import java.util.ArrayList;

public class HistoryTripsListAdapter extends RecyclerView.Adapter<HistoryTripsListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Trips> tripsList;
    private int duration;
    private Context context;

    public HistoryTripsListAdapter(Context context, ArrayList<Trips> tripsList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.tripsList = tripsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = layoutInflater.inflate(R.layout.history_card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Trips trip = tripsList.get(position);
        long tripId = trip.tripId ;
        String title = trip.tripName;
        /*populate data*/
        viewHolder.TripName.setText(title);
        viewHolder.StartingPoint.setText(trip.startPoint);
        viewHolder.Destination.setText(trip.endPoint);
        viewHolder.TripDate.setText(trip.tripDate);
        viewHolder.TripTime.setText(trip.tripTime);
        viewHolder.status.setText(trip.tripStatus);
        //viewHolder.Notes.setText(trip.);

                /*setting buttons actions */
        viewHolder.delete.setOnClickListener(v12 -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Warning")
                    .setMessage("On deleting this trip you won't be able to return it back again, delete it?")
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null)
                    .show();
            Button Yes = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Yes.setOnClickListener(v1 -> {
                /*Remove selected trip from Room*/
                AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                new Thread() {
                    @Override
                    public void run() {
                        /*remove from Room*/
                        db.tripDao().deleteTrip(trip);
                        /*remove form local list*/
                        tripsList.remove(position);
                        /*update UI*/
                        ((HomeActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });


                    }
                }.start();


                dialog.dismiss();
                //cancel the current work if it's not finished -REDUNDENT
                WorkManager.getInstance(context).cancelAllWorkByTag(trip.tripName);

            });

        });
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView TripName, StartingPoint, Destination, TripDate, TripTime, Notes,status;

        public ViewHolder(@NonNull View v) {
            super(v);
            TripName = v.findViewById(R.id.txt_tripName_cardview);
            StartingPoint = v.findViewById(R.id.txt_startingPoint_upcomingFrag);
            Destination = v.findViewById(R.id.txt_destination_cardview);
            TripDate = v.findViewById(R.id.txt_date_cardview);
            TripTime = v.findViewById(R.id.txt_startingTime_cardview);
            Notes = v.findViewById(R.id.txt_note_cardview);
            status = v.findViewById(R.id.txt_status_cardview);
            delete = v.findViewById(R.id.delete_btn);
        }
    }

}
