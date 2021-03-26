package com.iti.example.tripreminder;
/* ******************** *
 * Author: We'am  Kamal *
 * Date : 26th Mar 2021 *
 * ******************** */
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ViewTrip extends AppCompatActivity {
    Button ok;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trip);
        ok = findViewById(R.id.btn_cancel_editTrip);
        ok.setOnClickListener(v -> startActivity(new Intent(ViewTrip.this,HomeActivity.class)));
    }
}
