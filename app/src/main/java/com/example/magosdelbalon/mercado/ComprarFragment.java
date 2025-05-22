package com.example.magosdelbalon.mercado;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.Jugador;
import com.example.magosdelbalon.JugadorAdapter;
import com.example.magosdelbalon.MainActivity;
import com.example.magosdelbalon.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComprarFragment extends Fragment {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private String ligaName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_jugadores, container, false);

        recyclerView = view.findViewById(R.id.recyclerJugadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new JugadorAdapter(listaJugadores, jugador -> {
            Map<String, Object> jugadorMap = new HashMap<>();
            jugadorMap.put("nombre", jugador.getNombre());
            jugadorMap.put("posicion", jugador.getPosicion());
            jugadorMap.put("overall", jugador.getOverall());
            jugadorMap.put("precio", jugador.getPrecio());
            jugadorMap.put("url", jugador.getImageUrl());

            new FireStoreHelper().comprarJugador(ligaName, jugadorMap, new FireStoreHelper.FireStoreCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    // Refrescar dinero tras vender
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).refrescarDatosLiga();
                    }
                    fetchMercadoPlayers(); // Recargar lista tras compra
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }, JugadorAdapter.Modo.COMPRAR);

        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            ligaName = getArguments().getString("leagueName");
        }

        fetchMercadoPlayers();

        return view;
    }

    private void fetchMercadoPlayers() {
        new FireStoreHelper().fetchMercadoPlayers(ligaName, new FireStoreHelper.PlayersCallback() {
            @Override
            public void onPlayersLoaded(List<Jugador> players) {
                listaJugadores.clear();
                listaJugadores.addAll(players);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ComprarFragment", "Error al cargar mercado: " + errorMessage);
            }
        });
    }
}
