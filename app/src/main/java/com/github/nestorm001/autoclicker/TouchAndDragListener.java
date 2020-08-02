package com.github.nestorm001.autoclicker;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Responsible for dragging of the floating on off button.
 */
public class TouchAndDragListener implements View.OnTouchListener {
    private WindowManager.LayoutParams params;
    private int startDragDistance = 10;
    private ITouchAndDragListener listener;

    private int initialX = 0;
    private int initialY = 0;
    private float initialTouchX = 0;
    private float initialTouchY = 0;
    private boolean isDrag = false;

    /**
     *
     * @param params    The floating button's window.
     * @param startDragDistance If displacement of the floating button is higher than this,
     *                          we consider it as "being dragged".
     * @param listener  Listener for "touch" and "drag" events on the floating button.
     */
    public TouchAndDragListener(final WindowManager.LayoutParams params,
                                final int startDragDistance,
                                ITouchAndDragListener listener) {
        this.params = params;
        this.startDragDistance = startDragDistance;
        this.listener = listener;
    }
    public TouchAndDragListener(final WindowManager.LayoutParams params,
                                ITouchAndDragListener listener) {
        this(params, 10, listener);
    }

    /**
     * Checks if the button is dragged by comparing its movement to some treshold (startDragDistance).
     * @param event
     * @return
     */
    private boolean isDragging(MotionEvent event) {
        return ((Math.pow((double)(event.getRawX() - initialTouchX), 2.0)
                + Math.pow((double)(event.getRawY() - initialTouchY), 2.0))
                > startDragDistance * startDragDistance);
    }

    /**
     * Differentiates a drag event from a touch event.
     * If the button is being moved for a distance larger than the treshold "startDragDistance",
     * it is being dragged.
     * If it is being dragged, then MotionEvent.ACTION_MOVE is the start of a dragging, not a touch.
     * If it is not being dragged, then MotionEvent.ACTION_UP is a release of a click.
     *
     * If it is a click, call ITouchAndDragListener.onTouchAction.
     * If it is a drag, call ITouchAndDragListener.onDragAction.
     * @param v
     * @param event
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isDrag && isDragging(event)) {
                    isDrag = true;
                }
                if (!isDrag) return true;
                params.x = initialX + (int)(event.getRawX() - initialTouchX);
                params.y = initialY + (int)(event.getRawY() - initialTouchY);
                listener.onDragAction();
                return true;
            case MotionEvent.ACTION_UP:
                if (!isDrag) {
                    listener.onTouchAction();
                    return true;
                }
        }
        return false;
    }
}
