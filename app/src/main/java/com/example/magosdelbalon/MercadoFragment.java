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

                comprarJugador(ligaName, jugadorMap);
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

    private void comprarJugador(String ligaId, Map<String, Object> jugador) {
        FireStoreHelper helper = new FireStoreHelper();
        helper.comprarJugador(ligaId, jugador, new FireStoreHelper.FireStoreCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                // Refrescar datos tras compra exitosa
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refrescarDatosLiga();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
