package com.example.magosdelbalon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
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


    public static void enableImmersiveMode(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
