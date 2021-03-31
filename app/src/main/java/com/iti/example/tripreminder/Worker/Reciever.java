package com.iti.example.tripreminder.Worker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.iti.example.tripreminder.Models.Trips;
import com.iti.example.tripreminder.R;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.AppDatabase;
import com.iti.example.tripreminder.Repositiory.RoomDatabase.TripReminderDatabase;
import com.iti.example.tripreminder.Services.FloatingWidgetService;

import java.util.ArrayList;

public class Reciever extends BroadcastReceiver {
    private String tripId;
    private String tripName;
    @Override
    public void onReceive(Context context, Intent intent) {
        tripId = intent.getStringExtra(AddNewTripActivity.TRIP_ID);
        tripName = intent.getStringExtra(AddNewTripActivity.TRIP_NAME_KEY);
        ArrayList<Trips> tripsList;
        int notificationId = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title)
                .setMessage(tripName)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        // Toast.makeText(context, intent.getStringExtra(AddNewTripActivity.TRIP_ID), Toast.LENGTH_SHORT).show();
                        Log.i("tag", ""+tripId);
                        Log.i("msg", "fire");

                        AppDatabase db = TripReminderDatabase.getInstance((context)).getAppDatabase();
                        new Thread() {
                            @Override
                            public void run() {
                                db.tripDao().update(Long.parseLong(tripId), "History");
                                //trip added to local trips array list
                                //  notesListUpdater.sendMessage(new Message());
                            }
                        }.start();
                        // Creates an Intent that will load a map of San Francisco
                        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        // Attempt to start an activity that can handle the Intent
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        }

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


                        //cancel the current work
                        WorkManager.getInstance(context).cancelAllWorkByTag("reminder");


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i("msg", "cancel");
                        WorkManager.getInstance(context).cancelAllWorkByTag("reminder");
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
                        openDialogFromNotifcationIntent.putExtra(AddNewTripActivity.TRIP_ID,tripId);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, genId, openDialogFromNotifcationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                        /*create non-swipeable notification*/
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Trip Reminder")
                                .setContentText("Trip Info: this is Trip Reminder notification")
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
