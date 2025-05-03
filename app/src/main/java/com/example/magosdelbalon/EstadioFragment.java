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

        ImageButton btnEstadio = view.findViewById(R.id.btn_estadio);
        ImageButton btnCentroMedico = view.findViewById(R.id.btn_centro_medico);
        ImageButton btnCiudadDeportiva = view.findViewById(R.id.btn_ciudad_deportiva);

        btnEstadio.setOnClickListener(v -> mostrarDialogo("Estadio de Fútbol", "nivel_estadio"));
        btnCentroMedico.setOnClickListener(v -> mostrarDialogo("Centro Médico", "nivel_centro_medico"));
        btnCiudadDeportiva.setOnClickListener(v -> mostrarDialogo("Ciudad Deportiva", "nivel_ciudad_deportiva"));

        return view;
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
                                " de nivel " + currentLevel + " a nivel " + nextLevel + "?\nTe costará " + cost + " €.")
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
