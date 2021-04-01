package com.iti.example.tripreminder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.iti.example.tripreminder.Activities.EditTripActivity;
import com.iti.example.tripreminder.Activities.HomeActivity;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.ViewHolder> {
    private static final String TAG ="TripListAdapter";
    private LayoutInflater layoutInflater;
    private ArrayList<Trips> tripsList;
    private int duration;
    private Context context;
    Trips trip;
    Handler notesListUpdater;
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
          viewHolder.delete.setOnClickListener(v12 -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Warning")
                    .setMessage("On deleting this trip you won't be able to return it back again, delete it?")
                    .setPositiveButton("Yes",null)
                    .setNegativeButton("No",null)
                    .show();
            Button Yes = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Yes.setOnClickListener(v1 -> {
                trip = tripsList.get(viewHolder.getAdapterPosition());
                AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                new Thread(){
                    @Override
                    public void run() {
                        db.tripDao().deleteTrip(trip);
                        tripsList.remove(viewHolder.getAdapterPosition());
                        notesListUpdater.sendMessage(new Message());
                    }
                }.start();
                notesListUpdater = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);

                        notifyItemRemoved(viewHolder.getAdapterPosition());
                        notifyItemRangeChanged(viewHolder.getAdapterPosition(),tripsList.size());
                        notifyDataSetChanged();
                    }
                };
            });
        });
    }

    @Override
    public int getItemCount() {return tripsList.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        Intent edit_trip_intent,start_trip_intent;
        ImageButton delete,edit,start;
        TextView TripName,StartingPoint,Destination,TripDate,TripTime,Notes;
        public ViewHolder(@NonNull View v) {
            super(v);
            TripName = v.findViewById(R.id.txt_tripName_cardview);
            StartingPoint = v.findViewById(R.id.txt_startingTime_cardview);
            Destination = v.findViewById(R.id.txt_destination_cardview);
            TripDate = v.findViewById(R.id.txt_date_cardview);
            TripTime = v.findViewById(R.id.txt_startingTime_cardview);
            Notes = v.findViewById(R.id.txt_note_cardview);
            edit = v.findViewById(R.id.edit_btn);
            start = v.findViewById(R.id.start_btn);
            delete = v.findViewById(R.id.delete_btn);



            //Intents
            //start_trip_intent = new Intent(getActivity(),MapFragment.class);
            //OnClick Action Buttons
           // edit.setOnClickListener(v -> startActivity(edit_trip_intent));
            //start.setOnClickListener(v -> startActivity(start_trip_intent));
        }
    }
}
