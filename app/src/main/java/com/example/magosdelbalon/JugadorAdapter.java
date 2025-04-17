package com.example.magosdelbalon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JugadorAdapter extends RecyclerView.Adapter<JugadorAdapter.JugadorViewHolder> {

    private List<Jugador> jugadores;

    public JugadorAdapter(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @NonNull
    @Override
    public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jugador, parent, false);
        return new JugadorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
        Jugador jugador = jugadores.get(position);
        holder.nombre.setText(jugador.getNombre());
        holder.posicion.setText(jugador.getPosicion());
        holder.overall.setText("Overall: " + jugador.getOverall());
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public static class JugadorViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, posicion, overall;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textNombre);
            posicion = itemView.findViewById(R.id.textPosicion);
            overall = itemView.findViewById(R.id.textOverall);
        }
    }
}
