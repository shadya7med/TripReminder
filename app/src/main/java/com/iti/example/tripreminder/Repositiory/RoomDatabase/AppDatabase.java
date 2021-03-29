package com.iti.example.tripreminder.Repositiory.RoomDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.iti.example.tripreminder.Models.Trips;

@Database(entities = {Trips.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TripDao tripDao();
}
