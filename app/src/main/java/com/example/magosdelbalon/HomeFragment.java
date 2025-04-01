package com.example.magosdelbalon;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        // Cargar ligas automáticamente al inicio
        loadUserLigas(rootView);

        // Asignar listeners a los 4 botones
        setUpCreateLigaButton(rootView, R.id.btn_create_liga_1, 1);
        setUpCreateLigaButton(rootView, R.id.btn_create_liga_2, 2);
        setUpCreateLigaButton(rootView, R.id.btn_create_liga_3, 3);
        setUpCreateLigaButton(rootView, R.id.btn_create_liga_4, 4);

        return rootView;
    }

    private void setUpCreateLigaButton(View rootView, int buttonId, int ligaId) {
        View button = rootView.findViewById(buttonId);
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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void loadUserLigas(View rootView) {
        FireStoreHelper helper = new FireStoreHelper();
        helper.getUserLigas(new FireStoreHelper.LigasCallback() {
            @Override
            public void onLigasLoaded(List<Liga> ligas) {
                if (getActivity() == null) return; // Evita errores si el fragmento ya no está activo

                for (int i = 0; i < ligas.size(); i++) {
                    Liga liga = ligas.get(i);
                    int ligaId = i + 1;
                    String nombreLiga = liga.getNombre();

                    // Obtener IDs de los elementos
                    int textViewId = getResources().getIdentifier("liga_" + ligaId + "_nombre", "id", getActivity().getPackageName());
                    int imageViewId = getResources().getIdentifier("btn_create_liga_" + ligaId, "id", getActivity().getPackageName());
                    int layoutId = getResources().getIdentifier("liga_layout_" + ligaId, "id", getActivity().getPackageName());

                    TextView ligaTextView = rootView.findViewById(textViewId);
                    ImageView createButton = rootView.findViewById(imageViewId);
                    View ligaLayout = rootView.findViewById(layoutId); // Captura el cuadrado de la liga

                    if (ligaTextView != null) {
                        ligaTextView.setText(nombreLiga);
                        ligaTextView.setVisibility(View.VISIBLE);
                    }

                    if (createButton != null) {
                        if (liga.getNombre() != null && !liga.getNombre().isEmpty()) {
                            createButton.setVisibility(View.GONE); // Ocultar botón si la liga está ocupada

                            // Si la liga está ocupada, agregar click al cuadrado
                            if (ligaLayout != null) {
                                ligaLayout.setOnClickListener(v -> openLigaDetalleFragment(ligaId));
                            }
                        } else {
                            createButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getActivity(), "Error cargando ligas: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openLigaDetalleFragment(int ligaId) {
        Fragment ligaDetalleFragment = new PrincipalFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ligaDetalleFragment)
                .addToBackStack(null) // Permite volver atrás con el botón de retroceso
                .commit();
    }


    private void showCreateLigaDialog(int ligaId) {
        if (getActivity() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crear Liga");

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_liga, null);
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
            ligaSeleccionada[0] = "Premier";
            setSpinnerEquipos(spinnerEquipos, equiposPremier);
            imgLiga.setAlpha(0.3f);
            imgPremier.setAlpha(1f);
        });

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String ligaName = etLigaName.getText().toString().trim();
            String equipoName = spinnerEquipos.getSelectedItem() != null ? spinnerEquipos.getSelectedItem().toString() : "";

            if (ligaName.isEmpty() || equipoName.isEmpty() || ligaSeleccionada[0].isEmpty()) {
                Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                FireStoreHelper fireStoreHelper = new FireStoreHelper();
                fireStoreHelper.createLigaInFirestore(ligaId, ligaName, equipoName, ligaSeleccionada[0], new FireStoreHelper.FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        loadUserLigas(getView()); // Recargar ligas después de crear una
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setSpinnerEquipos(Spinner spinner, String[] equipos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
