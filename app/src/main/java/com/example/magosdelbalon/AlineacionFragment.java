package com.example.magosdelbalon;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class AlineacionFragment extends Fragment {

    // Constructor vacío
    public AlineacionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_alineacion, container, false);

        // Botones de jugadores
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

        // Setear onClickListener para los botones
        btnPortero.setOnClickListener(v -> onPlayerSelected("Portero"));
        btnDef1.setOnClickListener(v -> onPlayerSelected("Defensa 1"));
        btnDef2.setOnClickListener(v -> onPlayerSelected("Defensa 2"));
        btnDef3.setOnClickListener(v -> onPlayerSelected("Defensa 3"));
        btnDef4.setOnClickListener(v -> onPlayerSelected("Defensa 4"));
        btnMid1.setOnClickListener(v -> onPlayerSelected("Mediocentro 1"));
        btnMid2.setOnClickListener(v -> onPlayerSelected("Mediocentro 2"));
        btnMid3.setOnClickListener(v -> onPlayerSelected("Mediocentro 3"));
        btnMid4.setOnClickListener(v -> onPlayerSelected("Mediocentro 4"));
        btnFw1.setOnClickListener(v -> onPlayerSelected("Delantero 1"));
        btnFw2.setOnClickListener(v -> onPlayerSelected("Delantero 2"));

        return view;
    }

    // Método que maneja la selección de un jugador
    private void onPlayerSelected(String player) {
        // Muestra un mensaje o realiza una acción según la selección
        Toast.makeText(getContext(), "Jugador seleccionado: " + player, Toast.LENGTH_SHORT).show();
    }
}
