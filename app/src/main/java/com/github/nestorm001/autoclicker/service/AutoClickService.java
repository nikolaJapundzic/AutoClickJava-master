package com.github.nestorm001.autoclicker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import com.github.nestorm001.autoclicker.Extensions;
import com.github.nestorm001.autoclicker.MainActivity;
import com.github.nestorm001.autoclicker.bean.Event;

import android.graphics.Path;
import java.util.ArrayList;
import java.util.List;

public class AutoClickService extends AccessibilityService {
    private final List<Event> events = new ArrayList<Event>();  //Redundant. Only used in this class, in run method.
    /**
     * There is only one instance of this class need to exist at a time.
     * This field allows other parts of the application to know if autoClickService currently runs,
     * and if it does, this field lets them interact with that instance.
     */
    public static AutoClickService autoClickService = null;
    //---- AccessibilityService Methods Begin ----
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        // Do nothing
    }

    @Override
    public void onInterrupt() {
        // Do nothing
    }
    //---- AccessibilityService Methods End ----

    /**
     * The service is connected, so we have the permissions to create the overlay AND send clicks.
     * Start MainActivity again so it can start "FloatingClickService"
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Extensions.logd("onServiceConnected");
        autoClickService = this;
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * Creates a new click at the position (x, y).
     * It waits 10 milliseconds and then presses for 10 milliseconds.
     * This way it mimicks a single click.
     * @param x
     * @param y
     */
    public void click(int x, int y) {
        Extensions.logd(String.format("click %d %d", x, y));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        final Path path = new Path();
        path.moveTo(x, y);
        final GestureDescription.Builder builder = new GestureDescription.Builder();
        final GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 10, 10))
                .build();
        dispatchGesture(gestureDescription, null, null);
    }

    /**
     * Unused.
     * Given an array of events, it runs them sequentially.
     * @param newEvents
     */
    public void run(ArrayList<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        Extensions.logd(events.toString());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        final GestureDescription.Builder builder = new GestureDescription.Builder();
        for (Event event : events) {
            builder.addStroke(event.onEvent());
        }
        dispatchGesture(builder.build(), null, null);
    }

    /**
     * The most important function below is setting autoClickService to null.
     * Therefore other parts of the app will know it is unavailable.
     */
    //---- Methods for when autoClickService becomes invalid ----
    @Override
    public boolean onUnbind(Intent intent) {
        Extensions.logd("AutoClickService onUnbind");
        autoClickService = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Extensions.logd("AutoClickService onDestroy");
        autoClickService = null;
        super.onDestroy();
    }
}
