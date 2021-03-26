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

public class EditTrip extends AppCompatActivity {
    TextView tripName,notes,destination,date,time;
    Button save,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_trip);
        tripName = findViewById(R.id.txt_tripName_editTrip);
        notes = findViewById(R.id.txt_tripNotes_editTrip);
        destination = findViewById(R.id.txt_destination_editTrip);
        date = findViewById(R.id.txt_tripDate_editTrip);
        time = findViewById(R.id.txt_tripTime_editTrip);
        save = findViewById(R.id.btn_edit_editTrip);
        cancel = findViewById(R.id.btn_cancel_editTrip);

        notes.setOnClickListener(v -> startActivity(new Intent(EditTrip.this,AddNotes.class)));
        save.setOnClickListener(v -> { SaveEditedData(); startActivity(new Intent(EditTrip.this,HomeActivity.class));});
        cancel.setOnClickListener(v -> startActivity(new Intent(EditTrip.this,HomeActivity.class)));
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
