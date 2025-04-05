package com.example.magosdelbalon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    private static final String PREFS_NAME = "UserPreferences";
    private static final String KEY_BRIGHTNESS = "brightness";
    
    private Switch switchDarkMode;
    private SeekBar seekBarBrightness;
    private Button btnSaveSettings;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        switchDarkMode = rootView.findViewById(R.id.switch_dark_mode);
        seekBarBrightness = rootView.findViewById(R.id.seekbar_brightness);
        btnSaveSettings = rootView.findViewById(R.id.btn_save_settings);
        btnLogout = rootView.findViewById(R.id.btn_logout);

        // Cargar preferencias guardadas
        loadPreferences();

        // Configurar listener para el botón de guardar
        btnSaveSettings.setOnClickListener(v -> savePreferences());

        // Configurar listener para el botón de cerrar sesión
        btnLogout.setOnClickListener(v -> logout());

        // Configurar listener para el SeekBar de brillo
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

        return rootView;
    }

    private void loadPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        seekBarBrightness.setProgress(prefs.getInt(KEY_BRIGHTNESS, 50));
    }

    private void savePreferences() {
        saveBrightnessPreference(seekBarBrightness.getProgress());
        
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Preferencias guardadas", Toast.LENGTH_SHORT).show();
            // Volver al fragment anterior
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void saveBrightnessPreference(int brightness) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putInt(KEY_BRIGHTNESS, brightness);
        editor.apply();
    }

    private void setBrightness(int brightness) {
        float brightnessValue = brightness / 100f;
        WindowManager.LayoutParams layoutParams = requireActivity().getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessValue;
        requireActivity().getWindow().setAttributes(layoutParams);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), Inicio_Registro_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
} 