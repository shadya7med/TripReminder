package com.iti.example.tripreminder.Worker;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.iti.example.tripreminder.Activities.AddNewTripActivity;
import com.iti.example.tripreminder.Activities.HomeActivity;

public class MyWorker extends Worker {



    Context context ;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String tripName = getInputData().getString(AddNewTripActivity.TRIP_NAME_KEY);
        String tripId = getInputData().getString(AddNewTripActivity.TRIP_ID);
        String tripDestination = getInputData().getString(AddNewTripActivity.TRIP_DESTINATION);
        Intent actionIntent = new Intent();
        actionIntent.setAction(HomeActivity.ACTION);
        actionIntent.putExtra(AddNewTripActivity.TRIP_NAME_KEY,tripName);
        actionIntent.putExtra(AddNewTripActivity.TRIP_ID,tripId);
        actionIntent.putExtra(AddNewTripActivity.TRIP_DESTINATION,tripDestination);
        getApplicationContext().sendBroadcast(actionIntent);

        return Result.success();
    }
}
