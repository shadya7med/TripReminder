package com.iti.example.tripreminder.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class Notes {
    @PrimaryKey(autoGenerate = true)
    public long noteId;

    @ForeignKey(entity = Trips.class,parentColumns = "tripId",childColumns = "tId",onDelete = ForeignKey.CASCADE)
    public long tId;

    @ColumnInfo(name = "note_body")
    public String noteBody;

}
