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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Activities.EditTripActivity;
import com.iti.example.tripreminder.Activities.HomeActivity;
import com.iti.example.tripreminder.Activities.ViewTripActivity;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Services.FloatingWidgetService;

import java.util.ArrayList;

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Trips> tripsList;
    private int duration;
    private Context context;

    public TripsListAdapter(Context context, ArrayList<Trips> tripsList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.tripsList = tripsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = layoutInflater.inflate(R.layout.card_view, viewGroup, false);
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
        viewHolder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update Room with new data
                AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                new Thread() {
                    @Override
                    public void run() {
                        db.tripDao().update(tripId, AddNewTripActivity.TRIP_STATUS_STARTED);
                        //update local list
                        //trip added to local trips array list
                        //  notesListUpdater.sendMessage(new Message());
                        HomeActivity homeActivity = (HomeActivity) context;
                        UpComingFragment upComingFragment = (UpComingFragment) homeActivity.getSupportFragmentManager().findFragmentByTag(HomeActivity.DEFAULT_FRAGMENT);
                        homeActivity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .detach(upComingFragment)
                                .attach(upComingFragment)
                                .commit();
                    }
                }.start();
                // Creates an Intent that will load a map of San Francisco
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                // Attempt to start an activity that can handle the Intent
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
                //filled from Notes Entity in Room db
                new Thread() {
                    @Override
                    public void run() {
                        //filed Notes from Notes Table
                        ArrayList<String> notesList = new ArrayList<>();
                        notesList.add("this is th first");
                        notesList.add("this is the second");
                        notesList.add("this is the third");

                        /*show floating widget*/
                        Intent showNotesFWS = new Intent(context, FloatingWidgetService.class);
                        //send Notes List
                        showNotesFWS.putExtra(FloatingWidgetService.NOTES_LIST, notesList);
                        //start service
                        context.startService(showNotesFWS);
                    }
                }.start();




                //cancel the current work if it's not finished -REDUNDENT
                WorkManager.getInstance(context).cancelAllWorkByTag(""+trip.tripId);
            }
        });
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
                WorkManager.getInstance(context).cancelAllWorkByTag(""+trip.tripId);

            });

        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*open Edit Trip activity with current trip data*/
                Intent openEditTrip = new Intent(context, EditTripActivity.class);
                openEditTrip.putExtra(AddNewTripActivity.TRIP_INFO,trip);
                ((HomeActivity)context).startActivityForResult(openEditTrip,EditTripActivity.EDIT_TRIP_REQ_CODE);

            }
        });
        viewHolder.tripRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openTripDetails = new Intent(context, ViewTripActivity.class);
                openTripDetails.putExtra(AddNewTripActivity.TRIP_INFO,tripsList.get(position));
                context.startActivity(openTripDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Intent edit_trip_intent, start_trip_intent;
        ImageView delete, edit, start;
        TextView TripName, StartingPoint, Destination, TripDate, TripTime, Notes,status;
        CardView tripRow ;

        public ViewHolder(@NonNull View v) {
            super(v);
            TripName = v.findViewById(R.id.txt_tripName_cardview);
            StartingPoint = v.findViewById(R.id.txt_startingPoint_upcomingFrag);
            Destination = v.findViewById(R.id.txt_destination_cardview);
            TripDate = v.findViewById(R.id.txt_date_cardview);
            TripTime = v.findViewById(R.id.txt_startingTime_cardview);
            Notes = v.findViewById(R.id.txt_note_cardview);
            status = v.findViewById(R.id.txt_status_cardview);
            edit = v.findViewById(R.id.edit_btn);
            start = v.findViewById(R.id.start_btn);
            delete = v.findViewById(R.id.delete_btn);
            tripRow = v.findViewById(R.id.crdView_tripRow_card);
            //Intents
            // edit_trip_intent = new Intent(getActivity(),EditTripActivity.class);
            //start_trip_intent = new Intent(getActivity(),MapFragment.class);
            //OnClick Action Buttons
            // edit.setOnClickListener(v -> startActivity(edit_trip_intent));
            //start.setOnClickListener(v -> startActivity(start_trip_intent));
        }
    }

}
/*
* notesListUpdater = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        tripsList.remove(viewHolder.getAdapterPosition());
                        notifyItemRemoved(viewHolder.getAdapterPosition());
                        notifyItemRangeChanged(viewHolder.getAdapterPosition(), tripsList.size());
                        notifyDataSetChanged();
                    }
                };
* */