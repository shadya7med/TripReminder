package com.iti.example.tripreminder.Repositiory.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iti.example.tripreminder.Models.Notes;
import com.iti.example.tripreminder.Models.Trips;

import java.util.List;


/**
 * @Description:
 * Contains methods for each needed Query
 * @Usage:
 * Call the needed method with db instance anywhere from the App
 * */
@Dao
public interface TripDao {


    @Query("SELECT * FROM trips WHERE userId = :userId")
    List<Trips> getAllTripsForUser(String userId) ;

    @Query("SELECT * FROM trips WHERE trip_status = :status")
    List<Trips> getTripsByStatus(String status);

    @Query("SELECT * FROM trips WHERE trip_status = :status AND userId = :userId ")
    List<Trips> getTripsForUserByStatus(String userId,String status);

    @Query("SELECT * FROM trips WHERE tripId = :tripID")
    Trips getTripByID(int tripID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOne(Trips trip);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Trips> trips);

    @Delete
    void deleteTrip(Trips trip);

    @Update
    void update(Trips trip);

    @Query("UPDATE trips set trip_type = :sType WHERE tripId = :sID")
    void update(long sID,String sType);


    /*Notes DAO*/
    @Insert
    void insertNotes(List<Notes> notes);

}
