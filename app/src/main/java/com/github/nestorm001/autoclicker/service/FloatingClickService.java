package com.github.nestorm001.autoclicker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.nestorm001.autoclicker.Extensions;
import com.github.nestorm001.autoclicker.ITouchAndDragListener;
import com.github.nestorm001.autoclicker.R;
import com.github.nestorm001.autoclicker.TouchAndDragListener;

import java.util.Timer;
import java.util.TimerTask;

import static com.github.nestorm001.autoclicker.service.AutoClickService.autoClickService;

public class FloatingClickService extends Service {
    private WindowManager manager;
    private View view;
    private WindowManager.LayoutParams params;
    private int xForRecord = 0;
    private int yForRecord = 0;
    private final int[] location = new int[2];
    private int startDragDistance = 0;
    private Timer timer;
    private boolean isOn = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates and prepares floating on off button.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        startDragDistance = Extensions.dp2px(this,10f);
        view = LayoutInflater.from(this).inflate(R.layout.widget, null);

        //setting the layout parameters
        final int overlayParam;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            overlayParam = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
            overlayParam = WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //getting windows services and adding the floating view to it
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        manager.addView(view, params);

        //adding an touchlistener to make drag movement of the floating widget
        view.setOnTouchListener(new TouchAndDragListener(params, startDragDistance, new ITouchAndDragListener() {
                @Override
                public void onTouchAction() {
                    viewOnClick();
                }
                @Override
                public void onDragAction() {
                    manager.updateViewLayout(view, params);
                }
            }
        ));
    }

    /**
     * Called when the floating on off button is clicked.
     * It creates or destroys a timer so that screen is clicked at some point at 200ms interval.
     * In short, it turns on and off the clicking function.
     */
    private void viewOnClick() {
        if (isOn) {
            if (timer != null) {
                timer.cancel();
            }
        } else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    view.getLocationOnScreen(location);
                    if (autoClickService != null) {
                        autoClickService.click(location[0] + ((TextView) view).getRight() + 10,
                                location[1] + ((TextView) view).getBottom() + 10);
                    }
                }
            }, 0, 200);
        }
        isOn = !isOn;
        ((TextView) view).setText(isOn ? "ON" : "OFF");
    }

    /**
     * Removes the floating on off button from screen.
     * Cancels the timer so that clicking stops.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Extensions.logd("FloatingClickService onDestroy");
        if (timer != null) {
            timer.cancel();
        }
        manager.removeView(view);
    }

    /**
     * What is considered a "configuration change" is defined in AndroidManifest.
     * This service is registered to "orientation" changes.
     * In this application, if orientation changes, floating button's position will be messed up.
     * For good user experience, it is better to save two positions for floating button:
     * One for vertical device orientation and one or horizontal device orientation.
     * This listener switches between these two positions.
     *
     * In other words, when orientation changes, the last known position of the button is recorded.
     * When orientation changes again, this position is reloaded.
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Extensions.logd("FloatingClickService onConfigurationChanged");
        final int x = params.x;
        final int y = params.y;
        params.x = xForRecord;
        params.y = yForRecord;
        xForRecord = x;
        yForRecord = y;
        manager.updateViewLayout(view, params);
    }
}