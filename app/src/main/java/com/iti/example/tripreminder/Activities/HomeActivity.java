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
import android.provider.Settings;
import android.view.MenuItem;

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
import com.iti.example.tripreminder.Fragments.PastTripFragment;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Worker.Reciever;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String ACTION = "startReceiver";
    public static final String TRIP_NAME_KEY= "trip_name";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // First it confirms whether the
        // 'Display over other apps' permission in given
        if (checkOverlayDisplayPermission()) {
            toolbar = findViewById(R.id.toolbar_toolbar_nav_drawer_toolbar);
            navigationView = findViewById(R.id.navigationView);
            setSupportActionBar(toolbar);
            drawerLayout= findViewById(R.id.drawer);
            navigationView.setNavigationItemSelectedListener(this);

            actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();

            /*Create Notification channel for Android Oreo +*/
            createNotificationChannel();

            //register broadcast receiver
            BroadcastReceiver broadcastReceiver = new Reciever();
            IntentFilter testIntent = new IntentFilter(ACTION);
            registerReceiver(broadcastReceiver,testIntent);

            // load default fragment
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container_nav_content_main,new UpComingFragment(this));
            fragmentTransaction.commit();

            auth = FirebaseAuth.getInstance();

        } else {
            // If permission is not given,
            // it shows the AlertDialog box and
            // redirects to the Settings
            requestOverlayDisplayPermission();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //setContentView(R.layout.nav_drawer_header);
        drawerLayout.closeDrawer(GravityCompat.START);
       //emailAddress = findViewById(R.id.textView_email_nav_drawer_header);
      // emailAddress.setText(firebaseUser.getEmail());
        if(item.getItemId() == R.id.item_upcoming_nav_menu){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main,new UpComingFragment(this));
            fragmentTransaction.commit();
        }
        if(item.getItemId() == R.id.item_past_trip_nav_menu){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main,new PastTripFragment());
            fragmentTransaction.commit();
        }
        if(item.getItemId() == R.id.item_logout_nav_menu){
            auth.signOut();
             startActivity(new Intent(HomeActivity.this,LoginActivity.class));
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

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });
        AlertDialog dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (!checkOverlayDisplayPermission()) {
            requestOverlayDisplayPermission();
        }

    }*/
}