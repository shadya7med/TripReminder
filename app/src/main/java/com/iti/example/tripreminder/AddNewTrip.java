package com.iti.example.tripreminder;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddNewTrip extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,AdapterView.OnItemSelectedListener{
    private static final String TAG = "AddNewTripActivity";
    private static final String TAG2 = "Time Picker";
    TextInputLayout tripName,notes,destination;
    TextView my_date,my_time;
    Button add,cancel;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    String[] status = {"---Select Status---","UpComing","Done","Cancelled"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trip);
        tripName = findViewById(R.id.txt_name_newTrip);
        notes = findViewById(R.id.txt_notes_newTrip);
        destination = findViewById(R.id.txt_endPoint_newTrip);
        my_date = findViewById(R.id.txt_date_newTrip);
        my_time = findViewById(R.id.txt_time_newTrip);
        add = findViewById(R.id.btn_add_newTrip);
        cancel = findViewById(R.id.btn_cancel_newTrip);
        spinner = findViewById(R.id.spinner_newTrip);
        spinner.setOnItemSelectedListener(this);
        /* ************************************************************************************************************** */
        /* ******************************************** ON CLICK ******************************************************** */
        /* ************************************************************************************************************** */
        /*----------------------------------------*/
        /*-----------------1)Add------------------*/
        /*----------------------------------------*/
        add.setOnClickListener(v -> {
            SaveEditedData();
            startActivity(new Intent(AddNewTrip.this, HomeActivity.class));});
        /*-----------------------------------------*/
        /*-----------------2)Cancel----------------*/
        /*-----------------------------------------*/
        cancel.setOnClickListener(v -> startActivity(new Intent(AddNewTrip.this, HomeActivity.class)));
        /*-----------------------------------------*/
        /*-----------------3)Date------------------*/
        /*-----------------------------------------*/
        my_date.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(AddNewTrip.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
            String mydate = month + "/" + day + "/" + year;
            my_date.setText(mydate);
        };
        /*----------------------------------------*/
        /*-----------------4)Time-----------------*/
        /*----------------------------------------*/
        my_time.setOnClickListener(v -> {
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), TAG2);
        });
        /*----------------------------------------*/
        /*----------------5)Spinner---------------*/
        /*----------------------------------------*/
        // Create the instance of ArrayAdapter having the list of courses
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, status);
        // set simple layout resource file for each item of spinner
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the ArrayAdapter (arrayAdapter) data on the Spinner which binds data to spinner
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {my_time.setText(hourOfDay + ":" + minute);}
    @Override
    // Performing action when ItemSelected from spinner, Overriding onItemSelected method
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id){}
    // make toast of name of course which is selected in spinner
       // {Toast.makeText(getApplicationContext(),status[position], Toast.LENGTH_LONG).show();}
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}
    void SaveEditedData(){}

}
