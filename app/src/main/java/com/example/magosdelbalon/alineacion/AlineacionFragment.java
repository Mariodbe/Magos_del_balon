package com.example.magosdelbalon.alineacion;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.Jugador;
import com.example.magosdelbalon.MainActivity;
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
    private Map<String, String> selectedPlayers = new HashMap<>();
    private Map<String, TextView> textViews = new HashMap<>();

    public AlineacionFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alineacion, container, false);

        firestoreHelper = new FireStoreHelper();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("AlineacionFragment", "Usuario no autenticado");
            return view;
        }
        String userId = user.getUid();
        String ligaName = getArguments().getString("leagueName");

        // Asignar ImageViews y TextViews a cada posición
        imageViews.put("Goalkeeper", view.findViewById(R.id.img_portero));
        imageViews.put("Def1",       view.findViewById(R.id.img_def1));
        imageViews.put("Def2",       view.findViewById(R.id.img_def2));
        imageViews.put("Def3",       view.findViewById(R.id.img_def3));
        imageViews.put("Def4",       view.findViewById(R.id.img_def4));
        imageViews.put("Mid1",       view.findViewById(R.id.img_mid1));
        imageViews.put("Mid2",       view.findViewById(R.id.img_mid2));
        imageViews.put("Mid3",       view.findViewById(R.id.img_mid3));
        imageViews.put("Mid4",       view.findViewById(R.id.img_mid4));
        imageViews.put("Fw1",        view.findViewById(R.id.img_fw1));
        imageViews.put("Fw2",        view.findViewById(R.id.img_fw2));

        textViews.put("Goalkeeper", view.findViewById(R.id.txt_portero));
        textViews.put("Def1",       view.findViewById(R.id.txt_def1));
        textViews.put("Def2",       view.findViewById(R.id.txt_def2));
        textViews.put("Def3",       view.findViewById(R.id.txt_def3));
        textViews.put("Def4",       view.findViewById(R.id.txt_def4));
        textViews.put("Mid1",       view.findViewById(R.id.txt_mid1));
        textViews.put("Mid2",       view.findViewById(R.id.txt_mid2));
        textViews.put("Mid3",       view.findViewById(R.id.txt_mid3));
        textViews.put("Mid4",       view.findViewById(R.id.txt_mid4));
        textViews.put("Fw1",        view.findViewById(R.id.txt_fw1));
        textViews.put("Fw2",        view.findViewById(R.id.txt_fw2));

        firestoreHelper.cargarAlineacion(userId, ligaName, textViews, selectedPlayers);
        firestoreHelper.cargarAlineacionConImagenes(userId, ligaName, imageViews);

        // 3) Asignar clic normal a cada ImageView para abrir selector de jugador
        view.findViewById(R.id.img_portero).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Goalkeeper"));
        view.findViewById(R.id.img_def1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def1"));
        view.findViewById(R.id.img_def2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def2"));
        view.findViewById(R.id.img_def3).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def3"));
        view.findViewById(R.id.img_def4).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Def4"));
        view.findViewById(R.id.img_mid1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid1"));
        view.findViewById(R.id.img_mid2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid2"));
        view.findViewById(R.id.img_mid3).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid3"));
        view.findViewById(R.id.img_mid4).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Mid4"));
        view.findViewById(R.id.img_fw1).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw1"));
        view.findViewById(R.id.img_fw2).setOnClickListener(v -> openPlayerSelectionDialog(userId, ligaName, "Fw2"));

        setupLongClickPopUp(view, "Goalkeeper", R.id.card_player_portero);
        setupLongClickPopUp(view, "Def1",       R.id.card_player_def1);
        setupLongClickPopUp(view, "Def2",       R.id.card_player_def2);
        setupLongClickPopUp(view, "Def3",       R.id.card_player_def3);
        setupLongClickPopUp(view, "Def4",       R.id.card_player_def4);
        setupLongClickPopUp(view, "Mid1",       R.id.card_player_mid1);
        setupLongClickPopUp(view, "Mid2",       R.id.card_player_mid2);
        setupLongClickPopUp(view, "Mid3",       R.id.card_player_mid3);
        setupLongClickPopUp(view, "Mid4",       R.id.card_player_mid4);
        setupLongClickPopUp(view, "Fw1",        R.id.card_player_fw1);
        setupLongClickPopUp(view, "Fw2",        R.id.card_player_fw2);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al mostrarse este fragmento, ocultamos el menú superior de la Activity
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).ocultarMenuSuperior();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Al salir de este fragmento, volvemos a mostrar el menú superior
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mostrarMenus();
        }
    }

    /**
     * Configura un OnLongClickListener sobre el View identificado por cardId.
     * Cuando se mantiene presionado, infla dialog_player_card.xml y copia la imagen y el texto
     * desde los mapas imageViews/textViews para mostrar la carta ampliada en un AlertDialog.
     */
    private void setupLongClickPopUp(View rootView, String positionKey, int cardId) {
        View card = rootView.findViewById(cardId);
        if (card == null) return;

        card.setOnLongClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_player_card, null);

            ImageView dialogImg = dialogView.findViewById(R.id.dialog_img_player);
            TextView dialogTxt = dialogView.findViewById(R.id.dialog_txt_player);

            ImageView originalImg = imageViews.get(positionKey);
            TextView originalTxt = textViews.get(positionKey);
            if (originalImg != null && dialogImg != null) {
                dialogImg.setImageDrawable(originalImg.getDrawable());
            }
            if (originalTxt != null && dialogTxt != null) {
                dialogTxt.setText(originalTxt.getText());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(dialogView);
            builder.setPositiveButton("Cerrar", (dialogInterface, which) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            return true;
        });
    }

    /**
     * Abre un AlertDialog para seleccionar un jugador de la posición indicada.
     * Al seleccionar un jugador, actualiza la ImageView, el TextView y guarda la alineación.
     */
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
                    playerNames[i] = filtrados.get(i).getNombre() + " - " + filtrados.get(i).getOverall();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona un jugador");
                builder.setItems(playerNames, (dialog, which) -> {
                    Jugador selectedPlayer = filtrados.get(which);
                    String imageUrl = selectedPlayer.getImageUrl();

                    // Validar que no esté repetido en otra posición
                    for (Map.Entry<String, String> entry : selectedPlayers.entrySet()) {
                        if (!entry.getKey().equals(positionKey) &&
                                entry.getValue().equals(selectedPlayer.getNombre())) {
                            Toast.makeText(getContext(), "Ese jugador ya está en otra posición", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Log.d("PlayerSelection", "Jugador seleccionado: " + selectedPlayer.getNombre());
                    Log.d("PlayerSelection", "URL original de imagen: " + imageUrl);

                    // Cargar la imagen en la ImageView correspondiente
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String publicUrl = uri.toString();
                            Glide.with(getContext())
                                    .load(publicUrl)
                                    .into(imageViews.get(positionKey));

                            textViews.get(positionKey).setText(selectedPlayer.getNombre());
                            firestoreHelper.guardarAlineacionConImagenes(userId, leagueName, positionKey, publicUrl, () -> {
                                Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer.getNombre(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Error al obtener URL pública: " + e.getMessage());
                            Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Glide.with(getContext())
                                .load(imageUrl)
                                .into(imageViews.get(positionKey));

                        textViews.get(positionKey).setText(selectedPlayer.getNombre());
                        firestoreHelper.guardarAlineacionConImagenes(userId, leagueName, positionKey, imageUrl, () -> {
                            Toast.makeText(getContext(), "Jugador seleccionado: " + selectedPlayer.getNombre(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    selectedPlayers.put(positionKey, selectedPlayer.getNombre());
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
        if (key.startsWith("Def"))   return "Defensa";
        if (key.startsWith("Mid"))   return "Mediocentro";
        if (key.startsWith("Fw"))    return "Delantero";
        if (key.equals("Goalkeeper")) return "Portero";
        return "";
    }
}
