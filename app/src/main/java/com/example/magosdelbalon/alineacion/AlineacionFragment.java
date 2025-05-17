package com.example.magosdelbalon.alineacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
    private Map<String, ImageView> imageViews = new HashMap<>();

    public AlineacionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alineacion, container, false);

        firestoreHelper = new FireStoreHelper();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        String ligaName = getArguments().getString("leagueName");

        // ImageViews
        imageViews.put("Goalkeeper", view.findViewById(R.id.img_portero));
        imageViews.put("Def1", view.findViewById(R.id.img_def1));
        imageViews.put("Def2", view.findViewById(R.id.img_def2));
        imageViews.put("Def3", view.findViewById(R.id.img_def3));
        imageViews.put("Def4", view.findViewById(R.id.img_def4));
        imageViews.put("Mid1", view.findViewById(R.id.img_mid1));
        imageViews.put("Mid2", view.findViewById(R.id.img_mid2));
        imageViews.put("Mid3", view.findViewById(R.id.img_mid3));
        imageViews.put("Mid4", view.findViewById(R.id.img_mid4));
        imageViews.put("Fw1", view.findViewById(R.id.img_fw1));
        imageViews.put("Fw2", view.findViewById(R.id.img_fw2));

        // Cargar alineación con imágenes
        firestoreHelper.cargarAlineacionConImagenes(userId, ligaName, imageViews);

        // Botones
        view.findViewById(R.id.btn_portero).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Goalkeeper"));
        view.findViewById(R.id.btn_def1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def1"));
        view.findViewById(R.id.btn_def2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def2"));
        view.findViewById(R.id.btn_def3).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def3"));
        view.findViewById(R.id.btn_def4).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def4"));
        view.findViewById(R.id.btn_mid1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid1"));
        view.findViewById(R.id.btn_mid2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid2"));
        view.findViewById(R.id.btn_mid3).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid3"));
        view.findViewById(R.id.btn_mid4).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid4"));
        view.findViewById(R.id.btn_fw1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw1"));
        view.findViewById(R.id.btn_fw2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw2"));

        return view;
    }

    private void openPlayerSelectionDialog(String userId, String leagueName, String positionKey) {
        final String playerPosition = getGeneralPosition(positionKey);

        firestoreHelper.cargarJugadoresConImagenes(leagueName, new FireStoreHelper.JugadorImgListCallback() {
            @Override
            public void onSuccess(List<Jugador> jugadores) {
                List<Jugador> filtrados = new ArrayList<>();
                for (Jugador j : jugadores) {
                    if (j.getPosicion().equalsIgnoreCase(playerPosition)) {
                        filtrados.add(j);
                    }
                }

                // Crear un array de nombres de jugadores para el diálogo
                String[] playerNames = new String[filtrados.size()];
                for (int i = 0; i < filtrados.size(); i++) {
                    playerNames[i] = filtrados.get(i).getNombre();
                }

                // Mostrar el diálogo de selección de jugador
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona un jugador");
                builder.setItems(playerNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Jugador selectedPlayer = filtrados.get(which);
                        final String imageUrl = selectedPlayer.getImageUrl();
                        firestoreHelper.guardarAlineacionConImagenes(userId, leagueName, positionKey, imageUrl, new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = imageViews.get(positionKey);
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(getContext()).load(imageUrl).into(imageView);
                                } else {
                                    imageView.setImageResource(R.drawable.defaultplayer);
                                }
                                Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer.getNombre(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        firestoreHelper.saveLineup(userId, leagueName, positionKey, selectedPlayer.getNombre());
                    }
                });
                builder.show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }









    private String getGeneralPosition(String key) {
        if (key.startsWith("Def")) return "Defender";
        if (key.startsWith("Mid")) return "Midfielder";
        if (key.startsWith("Fw")) return "Forward";
        if (key.equals("Goalkeeper")) return "Goalkeeper";
        return "";
    }
}
