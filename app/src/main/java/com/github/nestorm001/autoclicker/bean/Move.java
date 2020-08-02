package com.github.nestorm001.autoclicker.bean;

import android.graphics.Point;

/**
 * Move Event.
 * Moves path to the given point.
 */
public class Move extends Event {
    Point toPoint;
    public Move(final Point to) {
        toPoint = to;
    }
    @Override
    public void movePath() {
        path.moveTo(toPoint.x, toPoint.y);
    }
}
