package com.iti.example.tripreminder.Activities;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputLayout;
import com.iti.example.tripreminder.Fragments.TimerPickerFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.Worker.MyWorker;
import com.iti.example.tripreminder.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AddNewTripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,AdapterView.OnItemSelectedListener{
    private static final String TAG = "AddNewTripActivity";
    private static final String TAG2 = "Time Picker";
    TextInputLayout tripName,notes,destination;
    TextView tripDateTextView , tripTimeTextView;
    Button add,cancel;
    DatePickerDialog.OnDateSetListener dateSetListener;

    Trips trip ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trip);
        tripName = findViewById(R.id.txt_name_newTrip);
        notes = findViewById(R.id.txt_notes_newTrip);
        destination = findViewById(R.id.txt_endPoint_newTrip);
        tripDateTextView = findViewById(R.id.txt_date_newTrip);
        tripTimeTextView = findViewById(R.id.txt_time_newTrip);
        add = findViewById(R.id.btn_add_newTrip);
        cancel = findViewById(R.id.btn_cancel_newTrip);

        /* ************************************************************************************************************** */
        /* ******************************************** ON CLICK ******************************************************** */
        /* ************************************************************************************************************** */
        /*----------------------------------------*/
        /*-----------------1)Add------------------*/
        /*----------------------------------------*/

        add.setOnClickListener(v -> {
            trip = saveEditedData();
            int duration = 10 ;
            Log.i("msg", "AddNew "+trip.tripName);
            //create data to hold trip name
            Data tripName = new Data.Builder()
                    .putString(HomeActivity.TRIP_NAME_KEY,trip.tripName)
                    .build();
            //create one time request
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setInputData(tripName)
                    .setInitialDelay(duration, TimeUnit.SECONDS)
                    .addTag(trip.tripName)
                    .build() ;
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
            Intent tripDataIntent = new Intent();
            tripDataIntent.putExtra(UpComingFragment.TRIP_INFO,trip);
            setResult(Activity.RESULT_OK,tripDataIntent);
            finish();
            });
        /*-----------------------------------------*/
        /*-----------------2)Cancel----------------*/
        /*-----------------------------------------*/
        cancel.setOnClickListener(v -> startActivity(new Intent(AddNewTripActivity.this, HomeActivity.class)));
        /*-----------------------------------------*/
        /*-----------------3)Date------------------*/
        /*-----------------------------------------*/
        tripDateTextView.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(AddNewTripActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
            String mydate = month + "/" + day + "/" + year;
            tripDateTextView.setText(mydate);
        };
        /*----------------------------------------*/
        /*-----------------4)Time-----------------*/
        /*----------------------------------------*/
        tripTimeTextView.setOnClickListener(v -> {
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), TAG2);
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tripTimeTextView.setText(hourOfDay + ":" + minute);}
    @Override
    // Performing action when ItemSelected from spinner, Overriding onItemSelected method
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id){}
    // make toast of name of course which is selected in spinner
       // {Toast.makeText(getApplicationContext(),status[position], Toast.LENGTH_LONG).show();}
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}
    Trips saveEditedData(){
        Trips trip = new Trips();
        trip.tripName = tripName.getEditText().getText().toString();
        trip.tripDate = tripDateTextView.getText().toString();
        trip.tripTime = tripTimeTextView.getText().toString();
        trip.tripType = "Upcoming";
        return trip;
    }



}