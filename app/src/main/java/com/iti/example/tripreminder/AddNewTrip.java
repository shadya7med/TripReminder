package com.iti.example.tripreminder;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewTrip extends AppCompatActivity {
    TextView tripName,notes,destination,date,time;
    Button add,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trip);
        tripName = findViewById(R.id.txt_name_newTrip);
        notes = findViewById(R.id.txt_note_newTrip);
        destination = findViewById(R.id.txt_destination_newTrip);
        date = findViewById(R.id.txt_date_newTrip);
        time = findViewById(R.id.txt_time_newTrip);
        add = findViewById(R.id.btn_add_newTrip);
        cancel = findViewById(R.id.btn_cancel_newTrip);

        notes.setOnClickListener(v -> startActivity(new Intent(AddNewTrip.this,AddNotes.class)));
        add.setOnClickListener(v -> { SaveEditedData(); startActivity(new Intent(AddNewTrip.this,HomeActivity.class));});
        cancel.setOnClickListener(v -> startActivity(new Intent(AddNewTrip.this,HomeActivity.class)));
    }
    void SaveEditedData(){
        String edited_name,edited_notes,edited_destination,edited_date,edited_time;
        edited_name = tripName.getText().toString();
        edited_notes = notes.getText().toString();
        edited_destination = destination.getText().toString();
        edited_date = date.getText().toString();
        edited_time = time.getText().toString();
    }
}
