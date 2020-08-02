package com.github.nestorm001.autoclicker;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * A static class to reuse a created Toast.
 * Toasts can only be shown on MainLooper.
 */
public class Toasts {
    private static Toast toast = null;
    private static void showToast(Context ctx, String text, int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (toast == null) {
                toast = Toast.makeText(ctx, text, duration);
            }
            toast.setText(text);
            toast.setDuration(duration);
            toast.show();
        } else {
            Extensions.loge("show toast run in wrong thread");
        }
    }
    private static void showToast(Context ctx, String text) {
        showToast(ctx, text, Toast.LENGTH_SHORT);
    }
    public static void errorToast(Context ctx, Throwable e) {
        showToast(ctx, e.getLocalizedMessage());
    }
    public static void longToast(Context ctx, String text) {
        showToast(ctx, text, Toast.LENGTH_LONG);
    }
    public static void longToast(Context ctx, int id) {
        showToast(ctx, ctx.getString(id), Toast.LENGTH_LONG);
    }
    public static void shortToast(Context ctx, String text) {
        showToast(ctx, text);
    }
    public static void shortToast(Context ctx, int id) {
        shortToast(ctx, ctx.getString(id));
    }
}
