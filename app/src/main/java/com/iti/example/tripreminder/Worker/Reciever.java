package com.iti.example.tripreminder.Worker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;

import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Activities.HomeActivity;
import com.iti.example.tripreminder.Fragments.UpComingFragment;
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Services.FloatingWidgetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reciever extends BroadcastReceiver {
    private String tripId;
    private String tripName;
    private String tripDestination;

    @Override
    public void onReceive(Context context, Intent intent) {
        tripId = intent.getStringExtra(AddNewTripActivity.TRIP_ID);
        tripName = intent.getStringExtra(AddNewTripActivity.TRIP_NAME_KEY);
        tripDestination = intent.getStringExtra(AddNewTripActivity.TRIP_DESTINATION);
        ArrayList<Trips> tripsList;
        int notificationId = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title)
                .setMessage(tripName)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        // Toast.makeText(context, intent.getStringExtra(AddNewTripActivity.TRIP_ID), Toast.LENGTH_SHORT).show();
                        Log.i("tag", "" + tripId);
                        Log.i("msg", "fire");

                        AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                        new Thread() {
                            @Override
                            public void run() {
                                db.tripDao().update(Long.parseLong(tripId), AddNewTripActivity.TRIP_STATUS_STARTED);
                                //trip added to local trips array list
                                //  notesListUpdater.sendMessage(new Message());
                                HomeActivity homeActivity = (HomeActivity) context;
                                UpComingFragment upComingFragment = (UpComingFragment) homeActivity.getSupportFragmentManager().findFragmentByTag(HomeActivity.DEFAULT_FRAGMENT);
                                homeActivity
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .detach(upComingFragment)
                                        .attach(upComingFragment)
                                        .commit();
                            }
                        }.start();
                        new Thread() {
                            @Override
                            public void run() {
                                // Creates an Intent that will load a map of San Francisco
                                Geocoder geocoder = new Geocoder(context);

                                try {
                                    List<Address> addresses = geocoder.getFromLocationName(tripDestination, 1);
                                    if(addresses.size() != 0){
                                        Log.i("msg", "" + addresses.get(0).getLongitude() + "latit:" + addresses.get(0).getLatitude());
                                        Uri gmmIntentUri = Uri.parse("geo:"+addresses.get(0).getLatitude()+","+addresses.get(0).getLongitude()+"?z=15");
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        // Attempt to start an activity that can handle the Intent
                                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                            context.startActivity(mapIntent);
                                        }
                                    }else{
                                        //do Nothing
                                    }


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }.start();



                        //filled from Database
                        new Thread() {
                            @Override
                            public void run() {
                                //filed Notes from Notes Table
                                ArrayList<String> notesList = new ArrayList<>();
                                notesList.add("this is th first");
                                notesList.add("this is the second");
                                notesList.add("this is the third");

                                /*show floating widget*/
                                Intent showNotesFWS = new Intent(context, FloatingWidgetService.class);
                                //send Notes List
                                showNotesFWS.putExtra(FloatingWidgetService.NOTES_LIST, notesList);
                                //start service
                                context.startService(showNotesFWS);
                            }
                        }.start();


                        //cancel the current work
                        WorkManager.getInstance(context).cancelAllWorkByTag(tripName);


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i("msg", "cancel");
                        AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                        new Thread() {
                            @Override
                            public void run() {
                                db.tripDao().update(Long.parseLong(tripId), AddNewTripActivity.TRIP_STATUS_CANCELED);
                                HomeActivity homeActivity = (HomeActivity) context;
                                UpComingFragment upComingFragment = (UpComingFragment) homeActivity.getSupportFragmentManager().findFragmentByTag(HomeActivity.DEFAULT_FRAGMENT);
                                homeActivity
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .detach(upComingFragment)
                                        .attach(upComingFragment)
                                        .commit();

                            }
                        }.start();
                        WorkManager.getInstance(context).cancelAllWorkByTag(tripName);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.neutral, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int genId = NotificationIDGenerator.getID();
                        Log.i("msg", "Snooze");
                        /*create pending Intent to show the dialog*/
                        Intent openDialogFromNotifcationIntent = new Intent();
                        openDialogFromNotifcationIntent.setAction(HomeActivity.ACTION);
                        openDialogFromNotifcationIntent.putExtra(AddNewTripActivity.TRIP_NAME_KEY, tripName);
                        openDialogFromNotifcationIntent.putExtra(AddNewTripActivity.TRIP_ID, tripId);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, genId, openDialogFromNotifcationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        /*create non-swipeable notification*/
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Trip Reminder")
                                .setContentText("Trip Name" + tripName)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setOngoing(true);//Ongoing to make notification NON-Swipable
                        /*show notification*/
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(genId, builder.build());
                        WorkManager.getInstance(context).cancelAllWorkByTag(tripName);
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Do something for Android Pie and above versions
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            // do something for phones running an SDK before Android Pie
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        alertDialog.show();

    }

}
