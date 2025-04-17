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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MercadoFragment extends Fragment {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();

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
        adapter = new JugadorAdapter(listaJugadores, jugador -> comprarJugador(jugador));
        recyclerView.setAdapter(adapter);

        // Suponiendo que tienes el tipo de liga guardado en algún lado
        String tipoLiga = "LaLiga"; // puedes obtenerlo de argumentos o SharedPreferences

        fetchMercadoPlayers(tipoLiga);

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

    private void comprarJugador(Jugador jugador) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaId = "tu_id_de_liga_hash"; // Reemplaza por cómo obtienes el ID/hash de la liga

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);
        DocumentReference ligaRef = db.collection("ligas").document(ligaId);

        userRef.get().addOnSuccessListener(userSnapshot -> {
            if (userSnapshot.exists()) {
                Map<String, Object> userData = userSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaId);
                    List<Map<String, Object>> jugadoresUsuario = (List<Map<String, Object>>) ligaData.get("jugadores");
                    int dineroActual = ((Number) ligaData.get("dinero")).intValue();

                    // Verificar si el jugador ya está en el equipo
                    for (Map<String, Object> jugadorMap : jugadoresUsuario) {
                        if (jugadorMap.get("nombre").equals(jugador.getNombre())) {
                            Toast.makeText(getContext(), "Ya tienes a este jugador.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Verificar dinero suficiente
                    if (jugador.getPrecio() > dineroActual) {
                        Toast.makeText(getContext(), "No tienes suficiente dinero.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Añadir jugador al equipo del usuario
                    Map<String, Object> nuevoJugador = new HashMap<>();
                    nuevoJugador.put("nombre", jugador.getNombre());
                    nuevoJugador.put("posicion", jugador.getPosicion());
                    nuevoJugador.put("overall", jugador.getOverall());
                    jugadoresUsuario.add(nuevoJugador);

                    ligaData.put("jugadores", jugadoresUsuario);
                    ligaData.put("dinero", dineroActual - jugador.getPrecio());
                    userData.put(ligaId, ligaData);

                    userRef.set(userData, SetOptions.merge()).addOnSuccessListener(unused -> {
                        // Luego de actualizar el usuario, eliminar el jugador del mercado
                        ligaRef.get().addOnSuccessListener(ligaSnapshot -> {
                            if (ligaSnapshot.exists()) {
                                Map<String, Object> ligaDoc = ligaSnapshot.getData();
                                List<Map<String, Object>> mercado = (List<Map<String, Object>>) ligaDoc.get("mercado");

                                if (mercado != null) {
                                    mercado.removeIf(j -> j.get("nombre").equals(jugador.getNombre()));
                                    ligaDoc.put("mercado", mercado);
                                    ligaRef.set(ligaDoc, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "¡Jugador comprado!", Toast.LENGTH_SHORT).show();
                                                fetchMercadoPlayers("LaLiga"); // O el tipoLiga real
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG, "Error al actualizar mercado: " + e.getMessage()));
                                }
                            }
                        });

                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error al guardar jugador en usuario: " + e.getMessage());
                    });
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error al acceder al usuario: " + e.getMessage()));
    }


}
