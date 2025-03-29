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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        rootView.findViewById(R.id.btn_create_liga_1).setOnClickListener(v -> showCreateLigaDialog(1));
        rootView.findViewById(R.id.btn_create_liga_2).setOnClickListener(v -> showCreateLigaDialog(2));

        return rootView;
    }

    private void showCreateLigaDialog(int ligaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crear Liga");

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_liga, null);
        builder.setView(dialogView);

        EditText etLigaName = dialogView.findViewById(R.id.et_liga_name);
        Spinner spinnerEquipos = dialogView.findViewById(R.id.spinner_equipo);
        ImageView imgLiga = dialogView.findViewById(R.id.btn_laliga);
        ImageView imgPremier = dialogView.findViewById(R.id.btn_premier);

        final String[] ligaSeleccionada = {""};

        // Arrays de equipos
        String[] equiposLaLiga = {
                "Real Madrid", "Barcelona", "Atlético de Madrid", "Sevilla", "Valencia",
                "Villarreal", "Real Sociedad", "Athletic Club", "Betis", "Osasuna",
                "Celta", "Espanyol", "Getafe", "Granada", "Mallorca",
                "Alavés", "Cádiz", "Elche", "Levante", "Rayo Vallecano"
        };

        String[] equiposPremier = {
                "Manchester City", "Liverpool", "Chelsea", "Arsenal", "Manchester United",
                "Tottenham", "Newcastle", "Brighton", "Aston Villa", "West Ham",
                "Leicester", "Everton", "Wolves", "Crystal Palace", "Fulham",
                "Bournemouth", "Southampton", "Leeds", "Nottingham Forest", "Brentford"
        };

        // Imágenes como botones de selección
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
                fireStoreHelper.createLigaInFirestore(ligaId, ligaName, equipoName, new FireStoreHelper.FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
