package com.iti.example.tripreminder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReminderService extends Service {
    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}