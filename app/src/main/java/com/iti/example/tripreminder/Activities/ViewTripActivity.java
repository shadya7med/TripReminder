package com.iti.example.tripreminder.Activities;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;

public class ViewTripActivity extends AppCompatActivity {
    Button ok;
    TextView tripName,startingPoint,destination,notes,dateValue,timeValue;
    Trips trip;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trip);
        /*Initializing widgets*/
        tripName = findViewById(R.id.txt_tripName_viewTrip);
        startingPoint = findViewById(R.id.txt_startingPoint_viewTrip);
        destination = findViewById(R.id.txt_destination_viewTrip);
        dateValue = findViewById(R.id.Date_textValue_viewTrip);
        timeValue = findViewById(R.id.Time_textValue_viewTrip);
        notes = findViewById(R.id.txt_notes_viewTrip);
        trip = (Trips) getIntent().getSerializableExtra(AddNewTripActivity.TRIP_INFO);

        /*populate TextViews with trip data*/
        tripName.setText(trip.tripName);
        startingPoint.setText(trip.startPoint);
        destination.setText(trip.endPoint);
        //notesEditText.setText(trip.);
        dateValue.setText(trip.tripDate);
        timeValue.setText(trip.tripTime);
        /*On Click Actions*/
        ok = findViewById(R.id.btn_ok_viewTrip);
        ok.setOnClickListener(v -> finish());
    }
}
