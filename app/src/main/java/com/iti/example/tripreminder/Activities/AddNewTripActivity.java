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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iti.example.tripreminder.Fragments.TimerPickerFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Worker.MyWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AddNewTripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddNewTripActivity";
    private static final String TAG2 = "Time Picker";
    public static final String TRIP_ID = "trip_id";
    public static final String TRIP_NAME_KEY = "trip_name";

    public static final String TRIP_STATUS_UPCOMING = "UPCOMING";
    public static final String TRIP_STATUS_STARTED = "STARTED";
    public static final String TRIP_STATUS_CANCELED = "CANCELED";


    TextInputLayout tripName, notes, destination;
    TextView tripDateTextView, tripTimeTextView;
    Button add, cancel;
    DatePickerDialog.OnDateSetListener dateSetListener;

    Trips trip;
    Handler notesListUpdater;

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
            trip.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String tripDate = trip.tripDate;
            String tripTime = trip.tripTime ;
            long duration = getDuration(tripDate,tripTime);
            //int duration = 10;//calculated from time and date
            Log.i("msg", "AddNew " + trip.tripName);
            //create data to hold trip name
            AppDatabase db = TripReminderDatabase.getInstance((this)).getAppDatabase();
            /*add trip data to Room*/
            new Thread() {
                @Override
                public void run() {
                    //trip inserted into db
                    long i = db.tripDao().insertOne(trip);
                    Message msg = notesListUpdater.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(TRIP_ID, String.valueOf(i));
                    msg.setData(bundle);
                    notesListUpdater.sendMessage(msg);
                    // Log.i("tag", String.valueOf(i[0]));
                }
            }.start();
            notesListUpdater = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    String id = bundle.getString(TRIP_ID);
                    Data tripName = new Data.Builder()
                            .putString(AddNewTripActivity.TRIP_NAME_KEY, trip.tripName)
                            .putString(AddNewTripActivity.TRIP_ID, String.valueOf(id)) //add destination
                            .build();
                    //create one time request
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                            .setInputData(tripName)
                            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                            .addTag(trip.tripName)
                            .build();
                    WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
                    Intent tripDataIntent = new Intent();
                    tripDataIntent.putExtra(UpComingFragment.TRIP_INFO, trip);
                    setResult(Activity.RESULT_OK, tripDataIntent);
                    finish();
                }
            };

        });
        /*-----------------------------------------*/
        /*-----------------2)Cancel----------------*/
        /*-----------------------------------------*/
        cancel.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
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
        tripTimeTextView.setText(hourOfDay + ":" + minute);

    }

    @Override
    // Performing action when ItemSelected from spinner, Overriding onItemSelected method
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
    }

    // make toast of name of course which is selected in spinner
    // {Toast.makeText(getApplicationContext(),status[position], Toast.LENGTH_LONG).show();}
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private Trips saveEditedData() {
        Trips trip = new Trips();
        trip.tripName = tripName.getEditText().getText().toString();
        trip.tripDate = tripDateTextView.getText().toString();
        trip.tripTime = tripTimeTextView.getText().toString();
        trip.tripStatus = AddNewTripActivity.TRIP_STATUS_UPCOMING;
        return trip;
    }
    private long getDuration(String tripDate, String tripTime) {
        String dateTime = tripDate+ " " + tripTime;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            date = formatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (date.getTime() - Calendar.getInstance().getTimeInMillis());
    }

}
