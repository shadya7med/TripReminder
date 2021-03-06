package com.iti.example.tripreminder.Activities;
/* ********************* *
 * Author: Abeer Mickawy *
 * Date : 20th Mar 2021  *
 * ********************* */

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iti.example.tripreminder.Fragments.PastTripFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Worker.Reciever;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String ACTION = "startReceiver";
    public static final String DEFAULT_FRAGMENT = "UPCOMING_FRAG";


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private FirebaseAuth firebaseAuth;
    TextView headerEmail;
    View headerView;
    AppDatabase db;
    DatabaseReference rtTripsRef;
    String userId;
    String userEmail;
    int prevItemId;

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
        fragmentTransaction.add(R.id.fragment_container_nav_content_main, new UpComingFragment(this), DEFAULT_FRAGMENT);
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
            prevItemId = item.getItemId();
        }
        if (item.getItemId() == R.id.item_past_trip_nav_menu) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main, new PastTripFragment(HomeActivity.this));
            fragmentTransaction.addToBackStack(null).commit();
            prevItemId = item.getItemId();
        }
        if (item.getItemId() == R.id.item_logout_nav_menu) {
            //Declaration and defination
            FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        //Do anything here which needs to be done after signout is complete
                        /*restart the App*/
                        /*Intent mStartActivity = new Intent(HomeActivity.this, LoginActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(HomeActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        prevItemId = item.getItemId();*/
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    } else {
                    }
                }
            };

            //Init and attach
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.addAuthStateListener(authStateListener);

            //Call signOut()
            firebaseAuth.signOut();



        }
        if (item.getItemId() == R.id.item_sync_nav_menu1) {
            new Thread() {
                @Override
                public void run() {
                    db = TripReminderDatabase.getInstance(HomeActivity.this).getAppDatabase();
                    List<Trips> trips = db.tripDao().getAllTripsForUser(userId);
                    if (trips.size() == 0) {
                        //fetch trips from RTDB
                        rtTripsRef = FirebaseDatabase.getInstance().getReference("Trips");
                        // Read from the database
                        rtTripsRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot TripsSnapShot) {
                                if (TripsSnapShot.exists()) {
                                    for (DataSnapshot tripData : TripsSnapShot.getChildren()) {
                                        Trips trip = tripData.getValue(Trips.class);
                                        trip.userId = userId;
                                        trips.add(trip);
                                    }
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            db.tripDao().insertAll(trips);
                                            if (prevItemId == R.id.item_past_trip_nav_menu) {
                                                fragmentManager
                                                        .beginTransaction()
                                                        .replace(R.id.fragment_container_nav_content_main, new PastTripFragment(HomeActivity.this))
                                                        .addToBackStack(null)
                                                        .commit();

                                            }else{
                                                fragmentManager
                                                        .beginTransaction()
                                                        .replace(R.id.fragment_container_nav_content_main, new UpComingFragment(HomeActivity.this))
                                                        .addToBackStack(null)
                                                        .commit();
                                            }

                                        }
                                    }.start();


                                } else {
                                    Toast.makeText(HomeActivity.this, "No Trips Stored", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.i("msg", "Failed to read value.", error.toException());
                            }
                        });
                    } else {
                        //replace RTDB with Room Content
                        rtTripsRef = FirebaseDatabase.getInstance().getReference("Trips");
                        rtTripsRef.child(userId).setValue(trips);

                    }
                }
            }.start();

            Toast.makeText(this, "Syncing Data...", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditTripActivity.EDIT_TRIP_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                UpComingFragment upComingFragment = (UpComingFragment) getSupportFragmentManager().findFragmentByTag(HomeActivity.DEFAULT_FRAGMENT);
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(upComingFragment)
                        .attach(upComingFragment)
                        .commit();
            }
        }
    }
}