package com.example.magosdelbalon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.magosdelbalon.alineacion.AlineacionMainFragment;
import com.example.magosdelbalon.mercado.MercadoFragment;
import com.example.magosdelbalon.principal.PrincipalMainFragment;
import com.example.magosdelbalon.video.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String ligaName; // <- esta será la liga activa durante toda la actividad
    FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private TextView ligaNombreTextView;
    private ImageView equipoLogoImageView;
    ImageView leagueLogoImageView;
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

        ImageView iconoVideo = findViewById(R.id.videoIconButton);

        // Obtener el tiempo de última visualización para la liga actual
        SharedPreferences prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE);
        long lastWatchedTime = prefs.getLong("lastWatched_" + ligaName, 0);
        long currentTime = System.currentTimeMillis();

        // Verificar si el botón debe estar visible o no
        boolean isButtonVisible = prefs.getBoolean("isVideoButtonVisible_" + ligaName, true);
        iconoVideo.setVisibility(isButtonVisible ? BottomNavigationView.VISIBLE : BottomNavigationView.INVISIBLE);

        if (currentTime - lastWatchedTime < 2 * 60 * 1000) {
            iconoVideo.setVisibility(BottomNavigationView.GONE);
        } else {
            iconoVideo.setVisibility(BottomNavigationView.VISIBLE);
        }

        iconoVideo.setOnClickListener(v -> {
            VideoFragment videoFragment = new VideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("leagueName", ligaName);
            videoFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, videoFragment)
                    .addToBackStack(null)
                    .commit();

            // Guarda el tiempo actual como última vez que vio el video para la liga actual
            SharedPreferences.Editor editor = getSharedPreferences("VideoPrefs", MODE_PRIVATE).edit();
            editor.putLong("lastWatched_" + ligaName, System.currentTimeMillis());
            editor.putBoolean("isVideoButtonVisible_" + ligaName, false);
            editor.apply();

            // Oculta el botón inmediatamente
            iconoVideo.setVisibility(BottomNavigationView.INVISIBLE);

            // Re-aparece después de 2 minutos
            new Handler().postDelayed(() -> {
                iconoVideo.setVisibility(BottomNavigationView.VISIBLE);
                editor.putBoolean("isVideoButtonVisible_" + ligaName, true);
                editor.apply();
            }, 2 * 60 * 1000);
        });

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
        equipoLogoImageView = findViewById(R.id.teamLogoImageView);
        dineroInicialTextView = findViewById(R.id.dineroInicialTextView);
        leagueLogoImageView = findViewById(R.id.leagueLogoImageView);


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
                initialFragment = crearPrincipalMainFragmentConLiga();
            } else {
                initialFragment = new PrincipalMainFragment(); // por si no hay liga
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, initialFragment)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_principal) {
                if (ligaName != null) {
                    selectedFragment = crearPrincipalMainFragmentConLiga();
                } else {
                    selectedFragment = new PrincipalMainFragment(); // fallback
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
                    selectedFragment = crearAlineacionMainFragmentConLiga();
                } else {
                    selectedFragment = new AlineacionMainFragment();
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

    private PrincipalMainFragment crearPrincipalMainFragmentConLiga() {
        PrincipalMainFragment fragment = new PrincipalMainFragment();
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

    private AlineacionMainFragment crearAlineacionMainFragmentConLiga() {
        AlineacionMainFragment fragment = new AlineacionMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("leagueName", ligaName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String formatearDinero(int cantidad) {
        if (cantidad >= 1_000_000) {
            double millones = cantidad / 1_000_000.0;
            return String.format(Locale.getDefault(), "%.1f M", millones).replace('.', ',');
        } else if (cantidad >= 1_000) {
            double miles = cantidad / 1_000.0;
            return String.format(Locale.getDefault(), "%.1f mil", miles).replace('.', ',');
        } else {
            return String.valueOf(cantidad);
        }
    }

    private String limpiarNombreParaDrawable(String nombre) {
        // Quita tildes
        String sinTildes = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Quita espacios y pasa a minúsculas
        return sinTildes.replaceAll("\\s+", "").toLowerCase();
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
                    int dinero = ((Number) dineroInicialObject).intValue();
                    dineroInicialText = formatearDinero(dinero);
                }

                if (dineroInicialText == null) {
                    dineroInicialText = "N/A";
                }

                if (ligaNombreTextView != null && equipoLogoImageView != null && dineroInicialTextView != null && leagueLogoImageView != null) {
                    ligaNombreTextView.setText("Liga: " + ligaName);
                    dineroInicialTextView.setText("Dinero: " + dineroInicialText);

                    // Cargar imagen del equipo
                    String nombreEquipoDrawable = limpiarNombreParaDrawable(equipo);
                    int resIdEquipo = getResources().getIdentifier(nombreEquipoDrawable, "drawable", getPackageName());

                    if (resIdEquipo != 0) {
                        equipoLogoImageView.setImageResource(resIdEquipo);
                    }

                    // Cargar imagen de la liga
                    String tipoLiga = (String) ligaData.get("tipoLiga");
                    String nombreLigaDrawable = limpiarNombreParaDrawable(tipoLiga);
                    int resIdLiga = getResources().getIdentifier(nombreLigaDrawable, "drawable", getPackageName());

                    if (resIdLiga != 0) {
                        leagueLogoImageView.setImageResource(resIdLiga);
                    }

                    Log.d("MainActivity", "Vistas actualizadas con logos equipo y liga");
                } else {
                    Log.e("MainActivity", "Views son null al actualizar datos liga");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MainActivity", "Error en Firestore: " + errorMessage);
                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void ocultarMenus() {
        findViewById(R.id.top_menu_container).setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Verificar el estado del botón al reanudar la actividad
        ImageView iconoVideo = findViewById(R.id.videoIconButton);
        SharedPreferences prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE);
        boolean isButtonVisible = prefs.getBoolean("isVideoButtonVisible_" + ligaName, true);
        iconoVideo.setVisibility(isButtonVisible ? BottomNavigationView.VISIBLE : BottomNavigationView.INVISIBLE);
    }

    public void mostrarMenus() {
        findViewById(R.id.top_menu_container).setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void refrescarDatosLiga() {
        if (ligaName != null) {
            obtenerDatosLiga(ligaName);
        }
    }

}

