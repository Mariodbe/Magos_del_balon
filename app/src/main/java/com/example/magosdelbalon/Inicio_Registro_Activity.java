package com.example.magosdelbalon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio_Registro_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Utils.applyUserBrightness(this);
        setContentView(R.layout.activity_inicio_registro);
        // Ocultar la barra de acción (ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configurar la ventana para un diseño de pantalla completa
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);


        loginButton.setOnClickListener(v -> {
            // Navegar a la actividad de login
            Intent intent = new Intent(Inicio_Registro_Activity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            // Navegar a la actividad de registro (crear esta actividad más adelante)
            Intent intent = new Intent(Inicio_Registro_Activity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Ajustes de las barras del sistema (como antes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registro_inicio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean musicEnabled = prefs.getBoolean("music_enabled", true);
        if (musicEnabled) {
            Intent musicServiceIntent = new Intent(this, MusicService.class);
            startService(musicServiceIntent);
        }


    }
}