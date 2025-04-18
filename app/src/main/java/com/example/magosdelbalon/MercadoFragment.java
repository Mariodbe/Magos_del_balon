package com.example.magosdelbalon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MercadoFragment extends Fragment {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private String ligaName;

    private static final String TAG = "MercadoFragment";

    public MercadoFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mercado, container, false);

        recyclerView = view.findViewById(R.id.recyclerMercado);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new JugadorAdapter(listaJugadores, jugador -> {
            if (ligaName != null) {
                Map<String, Object> jugadorMap = new HashMap<>();
                jugadorMap.put("nombre", jugador.getNombre());
                jugadorMap.put("posicion", jugador.getPosicion());
                jugadorMap.put("overall", jugador.getOverall());
                jugadorMap.put("precio", jugador.getPrecio());

                comprarJugador(ligaName, jugadorMap, new FireStoreHelper.FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Liga no seleccionada.", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            ligaName = getArguments().getString("leagueName");
        }

        if (ligaName == null) {
            Toast.makeText(getContext(), "Liga no seleccionada.", Toast.LENGTH_SHORT).show();
            return view;
        }

        fetchMercadoPlayers(ligaName);
        return view;
    }

    private void fetchMercadoPlayers(String tipoLiga) {
        FireStoreHelper helper = new FireStoreHelper();
        helper.fetchMercadoPlayers(tipoLiga, new FireStoreHelper.PlayersCallback() {
            @Override
            public void onPlayersLoaded(List<Jugador> players) {
                listaJugadores.clear();
                listaJugadores.addAll(players);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error al cargar mercado: " + errorMessage);
                Toast.makeText(getContext(), "Error al cargar jugadores del mercado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void comprarJugador(String ligaId, Map<String, Object> jugador, final FireStoreHelper.FireStoreCallback callback) {
        Log.d(TAG, "Entrando a comprarJugador con ligaId: " + ligaId + " y jugador: " + jugador.get("nombre"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        Log.d(TAG, "Usuario autenticado con ID: " + userId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Log.d(TAG, "documentSnapshot.exists(): " + documentSnapshot.exists());
            try {
                Map<String, Object> userData = documentSnapshot.getData();
                Log.d(TAG, "Datos completos del usuario: " + userData);

                if (userData != null && userData.containsKey(ligaId)) {
                    Object rawLigaData = userData.get(ligaId);

                    if (rawLigaData instanceof Map) {
                        Map<String, Object> ligaData = (Map<String, Object>) rawLigaData;
                        Log.d(TAG, "Liga encontrada: " + ligaData);

                        long dinero = ((Number) ligaData.get("dinero")).longValue();

                        // Precio estimado del jugador (puedes cambiar esto)
                        int precio2 = (int) jugador.get("precio");
                        long precio = precio2;

                        // Verificar si ya tiene el jugador
                        List<Map<String, Object>> jugadores = (List<Map<String, Object>>) ligaData.get("jugadores");
                        if (jugadores == null) jugadores = new ArrayList<>();

                        boolean yaTieneJugador = false;
                        for (Map<String, Object> j : jugadores) {
                            if (j.get("nombre").equals(jugador.get("nombre"))) {
                                yaTieneJugador = true;
                                break;
                            }
                        }

                        if (yaTieneJugador) {
                            callback.onFailure("Ya tienes a este jugador en tu equipo");
                            return;
                        }

                        // Verificar si tiene suficiente dinero
                        if (dinero < precio) {
                            callback.onFailure("No tienes suficiente dinero. Precio: " + precio);
                            return;
                        }

                        // Realizar la compra
                        jugadores.add(jugador);
                        ligaData.put("jugadores", jugadores);
                        ligaData.put("dinero", dinero - precio);

                        userRef.update(ligaId, ligaData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Jugador comprado correctamente");
                                    callback.onSuccess("Has comprado a " + jugador.get("nombre") + " por " + precio);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al actualizar jugadores: " + e.getMessage());
                                    callback.onFailure("Error al actualizar jugadores: " + e.getMessage());
                                });

                    } else {
                        Log.e(TAG, "El campo de la liga no es un Map");
                        callback.onFailure("Formato de liga incorrecto");
                    }
                } else {
                    Log.e(TAG, "Liga no encontrada en los datos del usuario");
                    callback.onFailure("Liga no encontrada para este usuario");
                }
            } catch (Exception e) {
                Log.e(TAG, "Excepción procesando datos del usuario", e);
                callback.onFailure("Error procesando datos del usuario: " + e.getMessage());
            }
        });
    }

}
