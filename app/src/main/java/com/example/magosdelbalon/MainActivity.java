package com.example.magosdelbalon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String ligaName; // <- esta será la liga activa durante toda la actividad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        ImageView iconoHome = findViewById(R.id.btn_back_to_home);
        iconoHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
                System.exit(0);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ligaName = getIntent().getStringExtra("ligaName"); // <- guardar en variable de clase
        Log.d("MainActivity", "onCreate: Intent recibido con ligaName: " + ligaName);

        if (savedInstanceState == null) {
            Fragment initialFragment;
            if (ligaName != null) {
                initialFragment = crearPrincipalFragmentConLiga();
            } else {
                initialFragment = new PrincipalFragment(); // por si no hay liga
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, initialFragment)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_principal) {
                if (ligaName != null) {
                    selectedFragment = crearPrincipalFragmentConLiga();
                } else {
                    selectedFragment = new PrincipalFragment(); // fallback
                }
            } else if (item.getItemId() == R.id.navigation_estadio) {
                if (ligaName != null) {
                    selectedFragment = crearEstadioFragmentConLiga();
                } else {
                    selectedFragment = new EstadioFragment();
                }
            }else if (item.getItemId() == R.id.navigation_entrenamiento) {
                if (ligaName != null) {
                    selectedFragment = crearEntrenamientoFragmentConLiga();
                } else {
                    selectedFragment = new EntrenamientoFragment();
                }
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    private PrincipalFragment crearPrincipalFragmentConLiga() {
        PrincipalFragment fragment = new PrincipalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }
    private EstadioFragment crearEstadioFragmentConLiga() {
        EstadioFragment fragment = new EstadioFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }
    private EntrenamientoFragment crearEntrenamientoFragmentConLiga() {
        EntrenamientoFragment fragment = new EntrenamientoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }

}

