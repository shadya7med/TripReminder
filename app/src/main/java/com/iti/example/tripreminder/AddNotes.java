package com.iti.example.tripreminder;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddNotes extends AppCompatActivity {
    TextView notes;
    Button add,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        notes = findViewById(R.id.txt_notes_addnotes);
        add = findViewById(R.id.btn_add_addnotes);
        cancel = findViewById(R.id.btn_cancel_addnotes);

        add.setOnClickListener(v -> {AddNotes(); startActivity(new Intent(AddNotes.this,HomeActivity.class));});
        cancel.setOnClickListener(v -> startActivity(new Intent(AddNotes.this, HomeActivity.class)));
    }
    void AddNotes(){
        String newNotes;
        newNotes = notes.getText().toString();
    }
}
