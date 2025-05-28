package com.example.magosdelbalon;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Aplicar modo oscuro
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Verificar si la música está activada
        boolean musicEnabled = prefs.getBoolean("music_enabled", true);
        if (musicEnabled) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }

        // Registrar el ciclo de vida de las actividades
        registerActivityLifecycleCallbacks(this);
    }

    // Se llama cuando una actividad se inicia
    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
    }

    // Se llama cuando una actividad se detiene
    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            // No quedan actividades activas, detener la música
            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
        }
    }

    // Métodos requeridos pero no usados
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
