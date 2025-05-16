package com.example.magosdelbalon;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView profileImage;
    private ImageButton btnSettings;

    // ActivityResultLauncher para la selección de imagen
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Aplicar circleCrop inmediatamente con Glide
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(profileImage);

                    // Subir la imagen a Firebase Storage
                    FireStoreHelper helper = new FireStoreHelper();
                    helper.uploadProfileImage(uri, new FireStoreHelper.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            Toast.makeText(HomeActivity.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(HomeActivity.this, "Error al subir la imagen: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();
        // Ocultar la barra de acción (ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity(); // Cierra todas las actividades
                System.exit(0);   // Forzar cierre si lo ves necesario (opcional)
            }
        });

        // Configurar la ventana para un diseño de pantalla completa
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        // Inicializar la imagen de perfil y otros componentes
        profileImage = findViewById(R.id.profile_image);
        if (profileImage != null) {
            profileImage.setOnClickListener(v -> openImagePicker());
            FireStoreHelper helper = new FireStoreHelper();
            helper.getProfileImage(profileImage);
        }

        btnSettings = findViewById(R.id.btn_settings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> openSettings());
        }
        // Verificar si hay un usuario autenticado antes de cargar ligas
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            loadUserLigas();
        }


        // Configurar los botones de ligas
        setUpCreateLigaButton(findViewById(R.id.btn_create_liga_1), 1);
        setUpCreateLigaButton(findViewById(R.id.btn_create_liga_2), 2);
        setUpCreateLigaButton(findViewById(R.id.btn_create_liga_3), 3);
        setUpCreateLigaButton(findViewById(R.id.btn_create_liga_4), 4);
    }
    private void openImagePicker() {
        getContent.launch("image/*");
    }

    private void openSettings() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
    private void setUpCreateLigaButton(View button, int ligaId) {
        if (button != null) {
            button.setOnClickListener(v -> {
                FireStoreHelper helper = new FireStoreHelper();
                helper.checkUserHasLiga(ligaId, new FireStoreHelper.FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        showCreateLigaDialog(ligaId);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }


    private void loadUserLigas() {
        FireStoreHelper helper = new FireStoreHelper();
        helper.getUserLigas(new FireStoreHelper.LigasCallback() {
            @Override
            public void onLigasLoaded(List<Liga> ligas) {
                if (HomeActivity.this == null) return; // Evita errores si el fragmento ya no está activo

                for (int i = 0; i < ligas.size(); i++) {
                    Liga liga = ligas.get(i);
                    int ligaId = i + 1;
                    String nombreLiga = liga.getNombre();

                    // Obtener IDs de los elementos
                    int textViewId = getResources().getIdentifier("liga_" + ligaId + "_nombre", "id", getPackageName());
                    int imageViewId = getResources().getIdentifier("btn_create_liga_" + ligaId, "id", getPackageName());
                    int layoutId = getResources().getIdentifier("liga_layout_" + ligaId, "id", getPackageName());

                    TextView ligaTextView = findViewById(textViewId);
                    ImageView createButton = findViewById(imageViewId);
                    View ligaLayout = findViewById(layoutId); // Captura el cuadrado de la liga

                    if (ligaTextView != null) {
                        ligaTextView.setText(nombreLiga);
                        ligaTextView.setVisibility(View.VISIBLE);
                    }

                    if (createButton != null) {
                        if (liga.getNombre() != null && !liga.getNombre().isEmpty()) {
                            createButton.setVisibility(View.GONE); // Ocultar botón si la liga está ocupada

                            // Si la liga está ocupada, agregar click al cuadrado
                            if (ligaLayout != null) {
                                ligaLayout.setOnClickListener(v -> openLigaDetalleFragment(nombreLiga)); // Pasamos el nombre de la liga
                            }
                        } else {
                            createButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HomeActivity.this, "Error cargando ligas: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openLigaDetalleFragment(String nombreLiga) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ligaName", nombreLiga); // Pasamos el nombre de la liga
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private void showCreateLigaDialog(int ligaId) {
        if (HomeActivity.this == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Crear Liga");

        View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_create_liga, null);
        builder.setView(dialogView);

        EditText etLigaName = dialogView.findViewById(R.id.et_liga_name);
        Spinner spinnerEquipos = dialogView.findViewById(R.id.spinner_equipo);
        ImageView imgLiga = dialogView.findViewById(R.id.btn_laliga);
        ImageView imgPremier = dialogView.findViewById(R.id.btn_premier);

        final String[] ligaSeleccionada = {""};

        String[] equiposLaLiga = {"Real Madrid", "Barcelona", "Atlético de Madrid"};
        String[] equiposPremier = {"Manchester City", "Liverpool", "Chelsea"};

        imgLiga.setOnClickListener(v -> {
            ligaSeleccionada[0] = "La Liga";
            setSpinnerEquipos(spinnerEquipos, equiposLaLiga);
            imgLiga.setAlpha(1f);
            imgPremier.setAlpha(0.3f);
        });

        imgPremier.setOnClickListener(v -> {
            ligaSeleccionada[0] = "Premier League";
            setSpinnerEquipos(spinnerEquipos, equiposPremier);
            imgLiga.setAlpha(0.3f);
            imgPremier.setAlpha(1f);
        });

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String rawLigaName = etLigaName.getText().toString().trim();

            if (rawLigaName.length() > 10) {
                Toast.makeText(HomeActivity.this, "El nombre de la liga no puede tener más de 10 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir a minúsculas, eliminar espacios y limpiar caracteres
            String ligaName = rawLigaName.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-z0-9]", "");
            String equipoName = spinnerEquipos.getSelectedItem() != null ? spinnerEquipos.getSelectedItem().toString() : "";

            if (ligaName.isEmpty() || equipoName.isEmpty() || ligaSeleccionada[0].isEmpty()) {
                Toast.makeText(HomeActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                FireStoreHelper fireStoreHelper = new FireStoreHelper();
                fireStoreHelper.createLigaInFirestore(ligaId, ligaName, equipoName, ligaSeleccionada[0], new FireStoreHelper.FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadUserLigas();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setSpinnerEquipos(Spinner spinner, String[] equipos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
