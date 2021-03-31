package com.iti.example.tripreminder.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * @Description:
 * Trips class represents trips table with columns
 *
 *
 * */

@Entity
public class Trips implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long tripId;

    @ColumnInfo(name = "trip_name")
    public String tripName ;

    @ColumnInfo(name = "start_point")
    public String startPoint ;

    @ColumnInfo(name = "end_point")
    public String endPoint ;

    @ColumnInfo(name = "trip_date")
    public String tripDate ;

    @ColumnInfo(name = "trip_time")
    public String tripTime ;

    @ColumnInfo(name = "trip_repetition")
    public String tripRepetition ;

    //notes should be added

    @ColumnInfo(name = "trip_type")
    public String tripType ;




}
