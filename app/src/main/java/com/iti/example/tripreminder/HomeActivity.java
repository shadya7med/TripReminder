package com.iti.example.tripreminder;
 /* ********************* *
  * Author: Abeer Mickawy *
  * Date : 20th Mar 2021  *
  * ********************* */
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

        toolbar = findViewById(R.id.toolbar_toolbar_nav_drawer_toolbar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        drawerLayout= findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        // load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_nav_content_main,new UpComingFragment());
        fragmentTransaction.commit();

        auth = FirebaseAuth.getInstance();

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
            fragmentTransaction.replace(R.id.fragment_container_nav_content_main,new UpComingFragment());
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
}