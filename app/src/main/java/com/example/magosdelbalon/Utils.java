package com.example.magosdelbalon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.WindowManager;

public class Utils {
    public static void applyUserBrightness(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("UserPreferences", activity.MODE_PRIVATE);
        int brightness = prefs.getInt("brightness", 50); // Valor por defecto 50
        float brightnessValue = brightness / 100f;

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessValue;
        activity.getWindow().setAttributes(layoutParams);
    }
}
