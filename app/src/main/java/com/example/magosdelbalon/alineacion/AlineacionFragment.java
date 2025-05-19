package com.example.magosdelbalon.alineacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.Jugador;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlineacionFragment extends Fragment {

    private FireStoreHelper firestoreHelper;
    private Map<String, ImageView> imageViews = new HashMap<>();

    public AlineacionFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alineacion, container, false);

        firestoreHelper = new FireStoreHelper();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("AlineacionFragment", "Usuario no autenticado");
            return view;
        }
        String userId = user.getUid();
        String ligaName = getArguments().getString("leagueName");

        // Asignar ImageViews
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

        // Cargar alineación guardada
        firestoreHelper.cargarAlineacionConImagenes(userId, ligaName, imageViews);

        // Asignar listeners a los botones
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

                String[] playerNames = new String[filtrados.size()];
                for (int i = 0; i < filtrados.size(); i++) {
                    playerNames[i] = filtrados.get(i).getNombre();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona un jugador");
                builder.setItems(playerNames, (dialog, which) -> {
                    Jugador selectedPlayer = filtrados.get(which);
                    String imageUrl = selectedPlayer.getImageUrl();

                    Log.d("PlayerSelection", "Jugador seleccionado: " + selectedPlayer.getNombre());
                    Log.d("PlayerSelection", "URL original de imagen: " + imageUrl);

                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String publicUrl = uri.toString();
                            // Usa Glide con la URL pública
                            Glide.with(getContext())
                                    .load(publicUrl)
                                    .into(imageViews.get(positionKey));

                            // Guarda esta URL en Firestore
                            firestoreHelper.guardarAlineacionConImagenes(userId, leagueName, positionKey, publicUrl, () -> {
                                Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer.getNombre(), Toast.LENGTH_SHORT).show();
                            });

                        }).addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Error al obtener URL pública: " + e.getMessage());
                            Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        // URL ya es pública
                        Glide.with(getContext())
                                .load(imageUrl)
                                .into(imageViews.get(positionKey));

                        firestoreHelper.guardarAlineacionConImagenes(userId, leagueName, positionKey, imageUrl, () -> {
                            Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer.getNombre(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    firestoreHelper.saveLineup(userId, leagueName, positionKey, selectedPlayer.getNombre());
                });
                builder.show();
            }

            @Override
            public void onFailure(String error) {
                Log.e("FirestoreHelper", "Error al cargar jugadores: " + error);
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
