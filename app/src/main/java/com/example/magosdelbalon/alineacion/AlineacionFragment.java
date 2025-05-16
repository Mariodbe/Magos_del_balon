package com.example.magosdelbalon.alineacion;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.Jugador;
import com.example.magosdelbalon.PlayerSelectionDialog;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlineacionFragment extends Fragment {

    private FireStoreHelper firestoreHelper;
    private Map<String, TextView> textViews = new HashMap<>();

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

        // TextViews
        TextView txtPortero = view.findViewById(R.id.txt_portero);
        TextView txtDef1 = view.findViewById(R.id.txt_def1);
        TextView txtDef2 = view.findViewById(R.id.txt_def2);
        TextView txtDef3 = view.findViewById(R.id.txt_def3);
        TextView txtDef4 = view.findViewById(R.id.txt_def4);
        TextView txtMid1 = view.findViewById(R.id.txt_mid1);
        TextView txtMid2 = view.findViewById(R.id.txt_mid2);
        TextView txtMid3 = view.findViewById(R.id.txt_mid3);
        TextView txtMid4 = view.findViewById(R.id.txt_mid4);
        TextView txtFw1 = view.findViewById(R.id.txt_fw1);
        TextView txtFw2 = view.findViewById(R.id.txt_fw2);

        // Asociar cada posición con su TextView
        textViews.put("Goalkeeper", txtPortero);
        textViews.put("Def1", txtDef1);
        textViews.put("Def2", txtDef2);
        textViews.put("Def3", txtDef3);
        textViews.put("Def4", txtDef4);
        textViews.put("Mid1", txtMid1);
        textViews.put("Mid2", txtMid2);
        textViews.put("Mid3", txtMid3);
        textViews.put("Mid4", txtMid4);
        textViews.put("Fw1", txtFw1);
        textViews.put("Fw2", txtFw2);


        // Cargar nombres desde Firestore
        firestoreHelper.cargarAlineacion(userId, ligaName, textViews);

        btnPortero.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Goalkeeper"));

        btnDef1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def1"));
        btnDef2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def2"));
        btnDef3.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def3"));
        btnDef4.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def4"));

        btnMid1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid1"));
        btnMid2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid2"));
        btnMid3.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid3"));
        btnMid4.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid4"));

        btnFw1.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw1"));
        btnFw2.setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw2"));

        return view;
    }



    private void openPlayerSelectionDialog(String userId, String leagueName, String positionKey) {
        String playerPosition = getGeneralPosition(positionKey); // ej: Fw1 → Forward

        firestoreHelper.cargarJugadoresDelUsuario(leagueName, new FireStoreHelper.JugadorListCallback() {
            @Override
            public void onSuccess(List<Jugador> jugadores) {
                // Filtra por posición si quieres
                List<Jugador> filtrados = new ArrayList<>();
                for (Jugador j : jugadores) {
                    if (j.getPosicion().equalsIgnoreCase(playerPosition)) {
                        filtrados.add(j);
                    }
                }

                PlayerSelectionDialog.show(getContext(), filtrados, selectedPlayer -> {
                    // Aquí lo demás sigue igual
                    for (Map.Entry<String, TextView> entry : textViews.entrySet()) {
                        if (!entry.getKey().equals(positionKey) &&
                                entry.getValue().getText().toString().equalsIgnoreCase(selectedPlayer)) {
                            Toast.makeText(getContext(), "Ese jugador ya está en otra posición", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    firestoreHelper.saveLineup(userId, leagueName, positionKey, selectedPlayer);
                    textViews.get(positionKey).setText(selectedPlayer);
                    Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Error cargando jugadores: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Traduce Fw1, Fw2 → Forward, Mid1 → Midfielder, etc.
    private String getGeneralPosition(String key) {
        if (key.startsWith("Def")) return "Defender";
        if (key.startsWith("Mid")) return "Midfielder";
        if (key.startsWith("Fw")) return "Forward";
        if (key.equals("Goalkeeper")) return "Goalkeeper";
        return ""; // posición desconocida
    }



}

