package com.example.magosdelbalon;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AlineacionFragment extends Fragment {

    private FireStoreHelper firestoreHelper;

    public AlineacionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alineacion, container, false);

        firestoreHelper = new FireStoreHelper();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Suponiendo que tienes el ID del usuario y el nombre de la liga almacenados en algún lugar
        String userId = user.getUid();

        String ligaName = getArguments().getString("leagueName");

        Button btnPortero = view.findViewById(R.id.btn_portero);
        Button btnDef1 = view.findViewById(R.id.btn_def1);
        Button btnDef2 = view.findViewById(R.id.btn_def2);
        Button btnDef3 = view.findViewById(R.id.btn_def3);
        Button btnDef4 = view.findViewById(R.id.btn_def4);
        Button btnMid1 = view.findViewById(R.id.btn_mid1);
        Button btnMid2 = view.findViewById(R.id.btn_mid2);
        Button btnMid3 = view.findViewById(R.id.btn_mid3);
        Button btnMid4 = view.findViewById(R.id.btn_mid4);
        Button btnFw1 = view.findViewById(R.id.btn_fw1);
        Button btnFw2 = view.findViewById(R.id.btn_fw2);

        btnPortero.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Goalkeeper"));
        btnDef1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Defender"));
        btnDef2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Defender"));
        btnDef3.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Defender"));
        btnDef4.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Defender"));
        btnMid1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Midfielder"));
        btnMid2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Midfielder"));
        btnMid3.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Midfielder"));
        btnMid4.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Midfielder"));
        btnFw1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Forward"));
        btnFw2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Forward"));

        return view;
    }



    private void openPlayerSelectionDialog(String userId, String leagueName, String position) {
        firestoreHelper.getPlayersByPosition(userId, leagueName, position, players -> {
            if (getActivity() != null) {
                PlayerSelectionDialog.show(getContext(), players, selectedPlayer -> {
                    // Guardar la alineación
                    firestoreHelper.saveLineup(userId, leagueName, position, selectedPlayer);
                    Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


}

