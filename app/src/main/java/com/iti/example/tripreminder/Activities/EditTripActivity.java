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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iti.example.tripreminder.Fragments.TimerPickerFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Worker.MyWorker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EditTripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "EditTripActivity";
    private static final String TAG2 = "Time Picker";
    TextInputLayout tripNameTextView,startingPointTextView,notesTextView,destinationTextView;
    TextInputEditText tripNameEditText,startingPointEditText,destinationEditText,notesEditText;
    ImageButton dateBtn,timeBtn;
    TextView dateTextView,timeTextView;
    Button save,cancel;
    Trips trip;
    DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_trip);
        tripNameTextView = findViewById(R.id.txt_tripName_editTrip);
        tripNameEditText = findViewById(R.id.edt_name_editTrip);
        startingPointTextView = findViewById(R.id.txt_startingPoint_editTrip);
        startingPointEditText = findViewById(R.id.edt_startingPoint_editTrip);
        destinationTextView = findViewById(R.id.txt_destination_editTrip);
        destinationEditText = findViewById(R.id.edt_destination_editTrip);
        notesTextView = findViewById(R.id.txt_note_editTrip);
        notesEditText = findViewById(R.id.edt_note_editTrip);
        dateBtn = findViewById(R.id.btn_date_editTrip);
        timeBtn = findViewById(R.id.btn_time_editTrip);
        dateTextView = findViewById(R.id.Date_textValue_editTrip);
        timeTextView = findViewById(R.id.Time_textValue_editTrip);
        save = findViewById(R.id.btn_add_editTrip);
        cancel = findViewById(R.id.btn_cancel_editTrip);

        //Initialize places
        Places.initialize(getApplicationContext(),"AIzaSyDL-OMMDIdvpwywXOGFbjncxF2nhCM2QUc");
        //set EditText nonFocusable
        startingPointEditText.setFocusable(false);
        destinationEditText.setFocusable(false);

        /* ************************************************************************************************************** */
        /* ******************************************** ON CLICK ******************************************************** */
        /* ************************************************************************************************************** */
        /*----------------------------------------*/
        /*------------ 1)StartingPoint -----------*/
        /*----------------------------------------*/
        startingPointEditText.setOnClickListener(v -> {
            // 1-Initialize Place Field
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
            // 2-Create intent
            Intent StartingPointIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(EditTripActivity.this);
            // 3-Start activity result
            startActivityForResult(StartingPointIntent,100);
        });
        /*----------------------------------------*/
        /*------------- 2)EndingPoint ------------*/
        /*----------------------------------------*/
        destinationEditText.setOnClickListener(v -> {
            // 1-Initialize Place Field
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
            // 2-Create intent
            Intent DestinationIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(EditTripActivity.this);
            // 3-Start activity result
            startActivityForResult(DestinationIntent,200);
        });
        /*----------------------------------------*/
        /*-----------------3)Save ----------------*/
        /*----------------------------------------*/
        save.setOnClickListener(v -> {
            trip = saveEditedData();
            int duration = 10 ;
            Log.i("msg", "Edit "+trip.tripName);
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
        /*-----------------4)Cancel----------------*/
        /*-----------------------------------------*/
        cancel.setOnClickListener(v -> startActivity(new Intent(EditTripActivity.this,HomeActivity.class)));
        /*-----------------------------------------*/
        /*-----------------5)Date------------------*/
        /*-----------------------------------------*/
        dateBtn.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(EditTripActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
            String mydate = month + "/" + day + "/" + year;
            dateTextView.setText(mydate);
        };
        /*----------------------------------------*/
        /*-----------------6)Time-----------------*/
        /*----------------------------------------*/
        timeBtn.setOnClickListener(v -> {
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), TAG2);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Case 1 (Starting Point)
        if(requestCode == 100 && resultCode == RESULT_OK){
            // when success initialize place
            Place StartingPlace = Autocomplete.getPlaceFromIntent(data);
            // set address on EditText
            startingPointEditText.setText(StartingPlace.getAddress());
        }
        // Case 2 (Destination)
        else if(requestCode == 200 && resultCode == RESULT_OK){
            // when success initialize place
            Place EndingPlace = Autocomplete.getPlaceFromIntent(data);
            // set address on EditText
            destinationEditText.setText(EndingPlace.getAddress());
        }
        // Case 3 (Error)
        else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            // Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            // Display Toast
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {timeTextView.setText(hourOfDay + ":" + minute);}
    @Override
    // Performing action when ItemSelected from spinner, Overriding onItemSelected method
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id){}
    // make toast of name of course which is selected in spinner
    // {Toast.makeText(getApplicationContext(),status[position], Toast.LENGTH_LONG).show();}
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}
    Trips saveEditedData(){
        Trips trip = new Trips();
        trip.tripName = tripNameEditText.toString();
        trip.startPoint = startingPointEditText.toString();
        trip.endPoint = destinationEditText.toString();
        trip.tripDate = dateTextView.getText().toString();
        trip.tripTime = timeTextView.getText().toString();
        trip.tripType = "Upcoming";
        return trip;
    }
}
