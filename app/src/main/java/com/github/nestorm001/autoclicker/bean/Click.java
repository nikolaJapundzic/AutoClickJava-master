package com.github.nestorm001.autoclicker.bean;

import android.graphics.Point;

/**
 * Click Event.
 * Its definition is the same as Move event.
 * Perhaps this is intended to show the difference in purposes of Click and Move events.
 * However this class is not used in the project.
 */
public class Click extends Event {
    Point toPoint;
    public Click(final Point to) {
        toPoint = to;
    }
    @Override
    public void movePath() {
        path.moveTo(toPoint.x, toPoint.y);
    }
}
