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

import java.util.ArrayList;
import java.util.List;

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
        adapter = new JugadorAdapter(listaJugadores);
        recyclerView.setAdapter(adapter);

        // Suponiendo que tienes el tipo de liga guardado en algún lado (puede ser un argumento, SharedPreferences, etc.)
        String tipoLiga = "LaLiga"; // o "PremierLeague", etc.

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
}
