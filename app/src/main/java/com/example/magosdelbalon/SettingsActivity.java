package com.example.magosdelbalon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPreferences";
    private static final String KEY_BRIGHTNESS = "brightness";
    private static final String KEY_DARK_MODE = "dark_mode";

    private Switch switchDarkMode;
    private SeekBar seekBarBrightness;
    private Button btnSaveSettings;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeFromPreferences(); // Aplicar modo oscuro/claro antes de cargar layout

        // Ocultar la barra de acción (ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configurar la ventana para un diseño de pantalla completa
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        seekBarBrightness = findViewById(R.id.seekbar_brightness);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnLogout = findViewById(R.id.btn_logout);

        // Cargar preferencias guardadas
        loadPreferences();

        // Guardar configuración
        btnSaveSettings.setOnClickListener(v -> savePreferences());

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> logout());

        // Cambiar brillo
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveBrightnessPreference(seekBar.getProgress());
            }
        });

        // Cambiar modo oscuro
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_DARK_MODE, isChecked);
            editor.apply();

            // Reiniciar para aplicar el tema
            recreate();
        });
    }

    private void applyThemeFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        seekBarBrightness.setProgress(prefs.getInt(KEY_BRIGHTNESS, 50));
        switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));
    }

    private void savePreferences() {
        saveBrightnessPreference(seekBarBrightness.getProgress());

        Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveBrightnessPreference(int brightness) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_BRIGHTNESS, brightness);
        editor.apply();
    }

    private void setBrightness(int brightness) {
        float brightnessValue = brightness / 100f;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessValue;
        getWindow().setAttributes(layoutParams);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(this, Inicio_Registro_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
