package com.example.magosdelbalon.alineacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;

public class TacticasFragment extends Fragment {

    private Button btnAgresividad;
    private Button btnPosesion;
    private Button btnPresion;
    private Button btnContraAtaques;

    private FireStoreHelper firestoreHelper;

    public TacticasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tacticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreHelper = new FireStoreHelper();

        btnAgresividad = view.findViewById(R.id.btn_agresividad);
        btnPosesion = view.findViewById(R.id.btn_posesion);
        btnPresion = view.findViewById(R.id.btn_presion);
        btnContraAtaques = view.findViewById(R.id.btn_contrataques);

        // Asegúrate de que el usuario está autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String leagueName = getArguments().getString("leagueName");

        btnAgresividad.setOnClickListener(v -> openTacticIntensityDialog(userId, leagueName, "Agresividad"));
        btnPosesion.setOnClickListener(v -> openTacticIntensityDialog(userId, leagueName, "Posesión"));
        btnPresion.setOnClickListener(v -> openTacticIntensityDialog(userId, leagueName, "Presión"));
        btnContraAtaques.setOnClickListener(v -> openTacticIntensityDialog(userId, leagueName, "Contraataques"));
    }

    private void openTacticIntensityDialog(String userId, String leagueName, String tacticKey) {
        String[] niveles = {"Muy Baja", "Baja", "Media", "Alta", "Muy Alta"};

        firestoreHelper.getTactic(userId, leagueName, tacticKey, new FireStoreHelper.FirestoreCallbackWithInt() {
            @Override
            public void onSuccess(int currentIntensity) {
                int checkedIndex = currentIntensity - 1; // intensidad va de 1 a 5, índice de 0 a 4

                final int[] seleccion = {checkedIndex};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona intensidad de " + tacticKey);
                builder.setSingleChoiceItems(niveles, checkedIndex, (dialog, which) -> seleccion[0] = which);

                builder.setPositiveButton("Aceptar", (dialog, which) -> {
                    int intensidad = seleccion[0] + 1;
                    firestoreHelper.saveTactic(userId, leagueName, tacticKey, intensidad);
                    Toast.makeText(getContext(), tacticKey + ": " + niveles[seleccion[0]], Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
