package com.example.magosdelbalon;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String ligaName; // <- esta será la liga activa durante toda la actividad
    FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private TextView ligaNombreTextView;
    private TextView equipoTextView;
    private TextView dineroInicialTextView;

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

        ligaName = getIntent().getStringExtra("ligaName"); // <- guardar en variable de clase

        ligaNombreTextView = findViewById(R.id.leagueNameTextView);
        equipoTextView = findViewById(R.id.teamNameTextView);
        dineroInicialTextView = findViewById(R.id.dineroInicialTextView);


        if (ligaName != null) {
            obtenerDatosLiga(ligaName);
        } else {
            Log.e("MainActivity", "ligaName es null, no se pudo continuar");
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
                System.exit(0);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

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
            }else if (item.getItemId() == R.id.navigation_mercado) {
                if (ligaName != null) {
                    selectedFragment = crearMercadoFragmentConLiga();
                } else {
                    selectedFragment = new MercadoFragment();
                }
            }else if (item.getItemId() == R.id.navigation_alineacion) {
                if (ligaName != null) {
                    selectedFragment = crearAlineacionFragmentConLiga();
                } else {
                    selectedFragment = new AlineacionFragment();
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
    private MercadoFragment crearMercadoFragmentConLiga() {
        MercadoFragment fragment = new MercadoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }
    private AlineacionFragment crearAlineacionFragmentConLiga() {
        AlineacionFragment fragment = new AlineacionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }
    private void obtenerDatosLiga(String ligaName) {
        FireStoreHelper helper = new FireStoreHelper();

        Log.d("MainActivity", "Llamando a obtenerDatosLigaPorId para: " + ligaName);

        helper.obtenerDatosLigaPorId(ligaName, new FireStoreHelper.FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                Log.d("MainActivity", "Datos obtenidos: " + ligaData);

                String equipo = (String) ligaData.get("equipo");
                Log.d("MainActivity", "Equipo recibido: " + equipo);

                Object dineroInicialObject = ligaData.get("dinero");
                String dineroInicialText = null;
                if (dineroInicialObject instanceof Number) {
                    dineroInicialText = String.valueOf(((Number) dineroInicialObject).intValue());
                }

                if(dineroInicialText == null){
                    dineroInicialText = "N/A";
                }

                // Asegúrate de que los TextView no sean null
                if (ligaNombreTextView != null && equipoTextView != null && dineroInicialTextView != null) {
                    ligaNombreTextView.setText("Liga: " + ligaName);
                    equipoTextView.setText("Equipo: " + equipo);
                    dineroInicialTextView.setText("Dinero inicial: " + dineroInicialText);

                    Log.d("MainActivity", "TextViews actualizados correctamente");
                } else {
                    Log.e("MainActivity", "TextViews son null al intentar actualizar");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MainActivity", "Error en Firestore: " + errorMessage);
                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void refrescarDatosLiga() {
        if (ligaName != null) {
            obtenerDatosLiga(ligaName);
        }
    }

}

