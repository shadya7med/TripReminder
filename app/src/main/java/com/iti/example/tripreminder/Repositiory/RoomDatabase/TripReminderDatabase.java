package com.iti.example.tripreminder.Repositiory.RoomDatabase;


import android.content.Context;

import androidx.room.Room;


/**
 * @Description:
 * singleton database class that builds the Room database
 * and returns shared instance from it
 * @Usage:
 * call static method getInstance to get database reference
 * to call with it DAO interface methods
 * */


public class TripReminderDatabase {

    private Context mCtx;
    private static TripReminderDatabase mInstance;
    public static final String DATABASE_NAME = "tripsDatabase.db";

    //our app database object
    private AppDatabase appDatabase;

    private TripReminderDatabase(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, DATABASE_NAME).build();
    }

    public static synchronized TripReminderDatabase getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new TripReminderDatabase(mCtx);
        }
        return mInstance;
    }


    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
