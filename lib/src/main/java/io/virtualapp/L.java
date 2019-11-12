package io.virtualapp;

import android.util.Log;

public class L {
    private static final boolean PRINT_LOG = true;
    private static final String TAG = "cylee_debug";

    public static void v(String msg) {
        if (PRINT_LOG) Log.v(TAG, msg);
    }

    public static void e(String msg) {
        if (PRINT_LOG) Log.e(TAG, msg);
    }
}
