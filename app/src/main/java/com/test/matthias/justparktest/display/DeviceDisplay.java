package com.test.matthias.justparktest.display;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Matthias on 13/12/2014.
 */
public final class DeviceDisplay {
    /**
     * Get the height of the status bar
     *
     * @return the height of the status bar in px
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Convert pixels into a float screen ratio (without status bar)
     *
     * @param context the application context
     * @param heightPixels the height in px to convert
     * @return the ratio between 0 and 1
     */
    public static float heightPixelsToRatio(Context context, float heightPixels) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int statusBarHeight = getStatusBarHeight(context);
        return (heightPixels + statusBarHeight) / (screenHeight - statusBarHeight);
    }
}
