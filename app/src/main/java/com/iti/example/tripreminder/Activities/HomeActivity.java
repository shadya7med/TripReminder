package com.iti.example.tripreminder.Activities;
/* ********************* *
 * Author: Abeer Mickawy *
 * Date : 20th Mar 2021  *
 * ********************* */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iti.example.tripreminder.Fragments.PastTripFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Worker.Reciever;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String ACTION = "startReceiver";
    public static final String TRIP_NAME_KEY = "trip_name";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FirebaseAuth auth;
    TextView headerEmail;
    View headerView;
    ArrayList<Trips> tripsList;
    AppDatabase db;
    DatabaseReference connectedRef;
    String userId;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_toolbar_nav_drawer_toolbar);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        headerEmail = headerView.findViewById(R.id.textView_email_nav_drawer_header);

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        /*Create Notification channel for Android Oreo +*/
        createNotificationChannel();

        //register broadcast receiver
        BroadcastReceiver broadcastReceiver = new Reciever();
        IntentFilter testIntent = new IntentFilter(ACTION);
        registerReceiver(broadcastReceiver, testIntent);

        // load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_nav_content_main, new UpComingFragment(this));
        fragmentTransaction.commit();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            headerEmail.setText(userEmail);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //setContentView(R.layout.nav_drawer_header);
        drawerLayout.closeDrawer(GravityCompat.START);
        //emailAddress = findViewById(R.id.textView_email_nav_drawer_header);
        // emailAddress.setText(firebaseUser.getEmail());
        if (item.getItemId() == R.id.item_upcoming_nav_menu) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main, new UpComingFragment(this));
            fragmentTransaction.addToBackStack(null).commit();
        }
        if (item.getItemId() == R.id.item_past_trip_nav_menu) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main, new PastTripFragment());
            fragmentTransaction.addToBackStack(null).commit();
        }
        if (item.getItemId() == R.id.item_logout_nav_menu) {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
        if (item.getItemId() == R.id.item_sync_nav_menu1) {
            db = TripReminderDatabase.getInstance(this).getAppDatabase();
            connectedRef = FirebaseDatabase.getInstance().getReference("Trips");
            new Thread(){
                @Override
                public void run() {
                    tripsList = (ArrayList<Trips>) db.tripDao().getAllTrips();
                    connectedRef.child(userId).setValue(tripsList);
                }
            }.start();
            Toast.makeText(this, "sync", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}