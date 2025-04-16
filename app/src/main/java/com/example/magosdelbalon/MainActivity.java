package com.example.magosdelbalon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String ligaName; // <- esta será la liga activa durante toda la actividad
    FireStoreHelper fireStoreHelper = new FireStoreHelper();

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
        ImageView iconoPapelera = findViewById(R.id.btn_papelera);
        iconoPapelera.setOnClickListener(v -> {
            if (ligaName != null) {
                // Mostrar un cuadro de diálogo de confirmación antes de eliminar
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Eliminar Liga")
                        .setMessage("¿Estás seguro de que deseas eliminar la liga '" + ligaName + "'?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Llamar al método deleteLiga
                            fireStoreHelper.deleteLiga(ligaName, new FireStoreHelper.FireStoreCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    // Mostrar mensaje de éxito
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    // Ir al HomeActivity o realizar alguna otra acción si lo deseas
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    // Mostrar mensaje de error
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                // Si no hay una liga activa, mostrar mensaje de error
                Toast.makeText(MainActivity.this, "No hay una liga activa para eliminar.", Toast.LENGTH_SHORT).show();
            }
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

