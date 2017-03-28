package com.bankicp.app.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static boolean isShow = true; //

    public static void showToast(Context context, CharSequence text, int duration) {
        if (isShow)
            Toast.makeText(context, text, duration).show();
    }

}
