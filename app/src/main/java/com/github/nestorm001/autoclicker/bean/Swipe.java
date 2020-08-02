package com.github.nestorm001.autoclicker.bean;

import android.graphics.Point;

/**
 * Swiping gesture event.
 * Given a "from" and a "to" points, it defines a swipe operation
 * from "from" to "to".
 */
public class Swipe extends Event {
    Point fromPoint;
    Point toPoint;
    public Swipe(final Point from, final Point to) {
        toPoint = to;
        fromPoint = from;
    }
    @Override
    public void movePath() {
        path.moveTo(fromPoint.x, fromPoint.y);
        path.lineTo(toPoint.x, toPoint.y);
    }
}
