package com.github.nestorm001.autoclicker;

import android.content.Context;
import android.util.Log;

public class Extensions {
    private static final String TAG = "AutoClickService";

    /**
     * Log functions that are only logging when it is a DEBUG build.
     */
    //---- BEGIN -----
    public static void logd(String msg, String tag) {
        if (!BuildConfig.DEBUG) return;
        Log.d(tag, msg);
    }
    public static void logd(Object msg) {
        logd(msg.toString(), TAG);
    }
    public static void loge(String msg, String tag) {
        if (!BuildConfig.DEBUG) return;
        Log.e(tag, msg);
    }
    public static void loge(Object msg) {
        loge(msg.toString());
    }
    //---- END ----

    /**
     * dp to pixel converter
     * @param ctx
     * @param dpValue input dp value
     * @return equivalent pixel value
     */
    public static int dp2px(Context ctx, Float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
