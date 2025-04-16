// EntrenamientoFragment.java
package com.example.magosdelbalon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class EntrenamientoFragment extends Fragment {

    private static final String TAG = "EntrenamientoFragment";

    private ListView listViewJugadores;
    private Button buttonEntrenar;
    private List<Jugador> listaJugadores;
    private ArrayAdapter<Jugador> adapter;

    private FireStoreHelper fireStoreHelper;

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenamiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewJugadores = view.findViewById(R.id.listViewJugadores);
        buttonEntrenar = view.findViewById(R.id.buttonEntrenar);

        fireStoreHelper = new FireStoreHelper();

        listaJugadores = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaJugadores);
        listViewJugadores.setAdapter(adapter);

        cargarJugadores();

        buttonEntrenar.setOnClickListener(v -> mostrarDialogoSeleccionJugador());
    }

    private void cargarJugadores() {
        String ligaName = getArguments().getString("leagueName");

        fireStoreHelper.obtenerJugadoresPorLiga(ligaName, new FireStoreHelper.JugadoresCallback() {
            @Override
            public void onJugadoresCargados(List<Jugador> jugadores) {
                listaJugadores.clear();
                listaJugadores.addAll(jugadores);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error al cargar jugadores", e);
                Toast.makeText(getContext(), "Error al cargar jugadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoSeleccionJugador() {
        if (listaJugadores.isEmpty()) {
            Toast.makeText(getContext(), "No tienes jugadores para entrenar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombresJugadores = new String[listaJugadores.size()];
        for (int i = 0; i < listaJugadores.size(); i++) {
            nombresJugadores[i] = listaJugadores.get(i).getNombre();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un jugador")
                .setItems(nombresJugadores, (dialog, which) -> entrenarJugador(listaJugadores.get(which)))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void entrenarJugador(Jugador jugador) {
        String ligaName = getArguments().getString("leagueName");
        if (ligaName == null || ligaName.isEmpty()) {
            Toast.makeText(getContext(), "Nombre de la liga no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        jugador.entrenar();

        fireStoreHelper.actualizarJugador(ligaName, jugador, new FireStoreHelper.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Jugador entrenado: " + jugador.getNombre() + ", nueva valoración: " + jugador.getOverall(), Toast.LENGTH_SHORT).show();
                cargarJugadores();
            }

            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "Error al entrenar jugador", e);
                Toast.makeText(getContext(), "Error al entrenar jugador", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
