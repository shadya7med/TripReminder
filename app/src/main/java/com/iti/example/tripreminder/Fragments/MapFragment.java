package com.iti.example.tripreminder.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MapFragment extends Fragment {
    SupportMapFragment mapFragment;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    private List<Trips> startedTripsList;
    private Handler notesListUpdater;
    private Context context;

    public MapFragment(){

    }
    public MapFragment(Context context){
        this.context = context ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        startedTripsList = new ArrayList<>();
        // get started Trips
        AppDatabase db = TripReminderDatabase.getInstance((getContext())).getAppDatabase();
        notesListUpdater = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        Toast.makeText(context, ""+getLocationFromAddress("Alexandria"), Toast.LENGTH_SHORT).show();
                        Log.i("msg",getLocationFromAddress("Alexandria")+"");

                        for ( int i= 0; i < startedTripsList.size(); i++) {
                            Random rnd = new Random();
                            int color= Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(getLocationFromAddress(startedTripsList.get(i).startPoint),
                                            getLocationFromAddress(startedTripsList.get(i).endPoint)).color(color));
                            polyline1.setTag("A");
                        }
                        //  stylePolyline(polyline1);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(30.033333,31.233334),7
                        ));

              /*  googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
                            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
                        } else {
                            // The default pattern is a solid stroke.
                            polyline.setPattern(null);
                        }
                    }
                });*/
                    }
                });
            }
        };
        new Thread() {
            @Override
            public void run() {
                startedTripsList.clear();
                List<Trips> startedTrips = db.tripDao().getTripsByStatus(AddNewTripActivity.TRIP_STATUS_STARTED);
                startedTripsList.addAll(startedTrips);
                notesListUpdater.sendMessage(new Message());
                

            }
        }.start();

    }

    /*private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }*/

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}