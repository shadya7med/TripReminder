package com.iti.example.tripreminder.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iti.example.tripreminder.Adapters.NotesListAdapter;
import com.iti.example.tripreminder.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by anupamchugh on 01/08/17.
 */

public class FloatingWidgetService extends Service {


    public static final String NOTES_LIST = "notes_list";
    public static final int NOTES_ON_GOING_NOTIFICATION_ID = 1;
    private WindowManager mWindowManager;
    private View mOverlayView;
    private RecyclerView notesListRcyView;
    private ArrayList<String> notesList;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*create non-swipeable notification*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Trip Reminder")
                .setContentText("Notes widget active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(true);//Ongoing to make notification NON-Swipable
        /*show notification*/
        startForeground(NOTES_ON_GOING_NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("msg", "onStartCommand: ");
        notesList = intent.getExtras().getStringArrayList(NOTES_LIST);

        setTheme(R.style.Theme_TripReminder);

        mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);


        notesListRcyView = mOverlayView.findViewById(R.id.rcyView_notesList_FWS);
        notesListRcyView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        notesListRcyView.setAdapter(new NotesListAdapter(getApplicationContext(), notesList));


        final WindowManager.LayoutParams params;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Do something for Android Pie and above versions
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            // do something for phones running an SDK before Android Pie
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);

        /*close button*/
        final View collapsedView = mOverlayView.findViewById(R.id.collapse_view);
        final View expandedView = mOverlayView.findViewById(R.id.expanded_container);
        ImageView closeButtonCollapsed = (ImageView) mOverlayView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });


        mOverlayView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        return true;
                    case MotionEvent.ACTION_UP:

                        //Add code for launching application and positioning the widget to nearest edge.
                        //xDiff and yDiff contain the minor changes in position when the view is clicked.
                        float xDiff = event.getRawX() - initialTouchX;
                        float yDiff = event.getRawY() - initialTouchY;

                        if (xDiff < 10 && yDiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            } else {
                                collapsedView.setVisibility(View.VISIBLE);
                                expandedView.setVisibility(View.GONE);
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:


                        if (!isViewCollapsed()) {
                            collapsedView.setVisibility(View.VISIBLE);
                            expandedView.setVisibility(View.GONE);
                        }

                        float Xdiff = Math.round(event.getRawX() - initialTouchX);
                        float Ydiff = Math.round(event.getRawY() - initialTouchY);


                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) Xdiff;
                        params.y = initialY + (int) Ydiff;

                        //Update the layout with new X & Y coordinates
                        mWindowManager.updateViewLayout(mOverlayView, params);


                        return true;
                }
                return false;
            }
        });


        return Service.START_STICKY;
    }

    private boolean isViewCollapsed() {
        return mOverlayView == null || mOverlayView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
        stopForeground(true);
    }


}
