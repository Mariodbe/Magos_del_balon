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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VenderFragment extends Fragment {

    private RecyclerView recyclerView;
    private JugadorAdapter adapter;
    private List<Jugador> jugadoresUsuario = new ArrayList<>();
    private String ligaName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_jugadores, container, false);

        recyclerView = view.findViewById(R.id.recyclerJugadores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new JugadorAdapter(jugadoresUsuario, jugador -> {
            new FireStoreHelper().venderJugador(ligaName, jugador, new FireStoreHelper.FireStoreCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    // Refrescar dinero tras vender
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).refrescarDatosLiga();
                    }
                    cargarJugadoresDelUsuario(); // Refrescar después de venta
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }, JugadorAdapter.Modo.VENDER);

        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            ligaName = getArguments().getString("leagueName");
        }

        cargarJugadoresDelUsuario();

        return view;
    }

    private void cargarJugadoresDelUsuario() {
        new FireStoreHelper().cargarJugadoresDelUsuario(ligaName, new FireStoreHelper.JugadorListCallback() {
            @Override
            public void onSuccess(List<Jugador> jugadores) {
                jugadoresUsuario.clear();
                jugadoresUsuario.addAll(jugadores);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
