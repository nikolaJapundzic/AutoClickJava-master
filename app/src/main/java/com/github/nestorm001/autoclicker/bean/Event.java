package com.github.nestorm001.autoclicker.bean;

import android.accessibilityservice.GestureDescription;

import android.graphics.Path;
import android.graphics.Point;

/**
 * Abstract class for gesture events.
 * These events are used to define "swiping", "clicking", "moving the cursor" on the screen.
 * Starting point, ending point, start delay, duration are its inputs.
 */
public abstract class Event {
    public long startTime = 10L;
    public long duration = 10L;
    public Path path;
    public GestureDescription.StrokeDescription onEvent() {
        path = new Path();
        movePath();
        return new GestureDescription.StrokeDescription(path, startTime, duration);
    }

    public abstract void movePath();
}

