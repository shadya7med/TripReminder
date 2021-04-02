package com.iti.example.tripreminder.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iti.example.tripreminder.R;

import java.util.ArrayList;

public class AddNotesActivity extends AppCompatActivity {

    EditText note1,note2,note3,note4,note5,note6,note7,note8;
    Button save;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);

        note1 = findViewById(R.id.note1);
        note2 = findViewById(R.id.note2);
        note3 = findViewById(R.id.note3);
        note4 = findViewById(R.id.note4);
        note5 = findViewById(R.id.note5);
        note6 = findViewById(R.id.note6);
        note7 = findViewById(R.id.note7);
        note8 = findViewById(R.id.note8);

        save = findViewById(R.id.btn_save_notes);

        save.setOnClickListener(v -> {
            ArrayList<String> notesList = new ArrayList<>();

            notesList.add(note1.getText().toString());
            notesList.add(note2.getText().toString());
            notesList.add(note3.getText().toString());
            notesList.add(note4.getText().toString());
            notesList.add(note5.getText().toString());
            notesList.add(note6.getText().toString());
            notesList.add(note7.getText().toString());
            notesList.add(note8.getText().toString());


            Intent returnNotesList = new Intent();
            returnNotesList.putExtra(AddNewTripActivity.NOTES_BODY_ARRAY,notesList);
            setResult(Activity.RESULT_OK,returnNotesList);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
