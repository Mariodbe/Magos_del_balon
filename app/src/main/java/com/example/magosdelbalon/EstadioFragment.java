package com.example.magosdelbalon;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EstadioFragment extends Fragment {

    private String ligaName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadio, container, false);

        if (getArguments() != null) {
            ligaName = getArguments().getString("leagueName");
        }

        if (ligaName == null) {
            Toast.makeText(getContext(), "Liga no seleccionada.", Toast.LENGTH_SHORT).show();
            return view;
        }

        ImageButton btn_estadio = view.findViewById(R.id.btn_estadio);
        ImageButton btnCentroMedico = view.findViewById(R.id.btn_centro_medico);
        ImageButton btnCiudadDeportiva = view.findViewById(R.id.btn_ciudad_deportiva);

        // Ya puedes usar btn_estadio de forma segura aquí:
        FireStoreHelper helper = new FireStoreHelper();
        helper.obtenerDatosLigaPorId(ligaName, new FireStoreHelper.FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                if (ligaData.containsKey("equipo")) {
                    String equipo = ligaData.get("equipo").toString();
                    int imagenEstadio = obtenerImagenEstadioPorEquipo(equipo);
                    btn_estadio.setImageResource(imagenEstadio);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        btn_estadio.setOnClickListener(v -> mostrarDialogo("Estadio de Fútbol", "nivel_estadio"));
        btnCentroMedico.setOnClickListener(v -> mostrarDialogo("Centro Médico", "nivel_centro_medico"));
        btnCiudadDeportiva.setOnClickListener(v -> mostrarDialogo("Ciudad Deportiva", "nivel_ciudad_deportiva"));

        return view;
    }
    private int obtenerImagenEstadioPorEquipo(String equipo) {
        switch (equipo) {
            case "Barcelona":
                return R.drawable.camp_nou;
            case "Real Madrid":
                return R.drawable.bernabeu;
            case "Atlético de Madrid":
                return R.drawable.estadio_metropolitano;
            case "Athletic Club":
                return R.drawable.san_mames;
            case "Manchester City":
                return R.drawable.estadio_city;
            case "Liverpool":
                return R.drawable.anfield;
            case "Chelsea":
                return R.drawable.stamford_bridge;
            case "Arsenal":
                return R.drawable.emirates;
            default:
                return R.drawable.ic_estadiomenu;
        }
    }

    private void mostrarDialogo(String nombreIcono, String nivelKey) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FireStoreHelper helper = new FireStoreHelper();

        helper.obtenerNivelEstadio(userId, ligaName, nivelKey, new FireStoreHelper.NivelCallback() {
            @Override
            public void onSuccess(int currentLevel) {

                if (currentLevel >= 10) {
                    Toast.makeText(getContext(), nombreIcono + " ya está al nivel máximo.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nextLevel = currentLevel + 1;
                long cost = 500000 + (currentLevel * 100000);

                new AlertDialog.Builder(getContext())
                        .setTitle("Mejora")
                        .setMessage("¿Quieres mejorar " + nombreIcono +
                                " de nivel " + currentLevel + " a nivel " + nextLevel + "?\nTe costará " + MainActivity.formatearDinero((int) cost) + " €.")
                        .setPositiveButton("Mejorar", (dialog, which) -> {
                            helper.upgradeEstadio(userId, ligaName, nivelKey, new FireStoreHelper.FireStoreCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).refrescarDatosLiga();
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
