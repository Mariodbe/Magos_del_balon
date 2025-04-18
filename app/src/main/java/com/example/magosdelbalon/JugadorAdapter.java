package com.example.magosdelbalon;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JugadorAdapter extends RecyclerView.Adapter<JugadorAdapter.JugadorViewHolder> {

    private List<Jugador> listaJugadores;
    private OnJugadorClickListener listener;

    public interface OnJugadorClickListener {
        void onComprarClick(Jugador jugador);
    }

    public JugadorAdapter(List<Jugador> listaJugadores, OnJugadorClickListener listener) {
        this.listaJugadores = listaJugadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jugador, parent, false);
        return new JugadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
        holder.bind(listaJugadores.get(position));
    }

    @Override
    public int getItemCount() {
        return listaJugadores.size();
    }

    class JugadorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPosicion, tvOverall, tvPrecio;
        Button btnComprar;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.textNombre);
            tvPosicion = itemView.findViewById(R.id.textPosicion);
            tvOverall = itemView.findViewById(R.id.textOverall);
            tvPrecio = itemView.findViewById(R.id.textPrecio);
            btnComprar = itemView.findViewById(R.id.btnComprar);
        }

        public void bind(Jugador jugador) {
            tvNombre.setText(jugador.getNombre());
            tvPosicion.setText(jugador.getPosicion());
            tvOverall.setText("OVR: " + jugador.getOverall());
            tvPrecio.setText("Precio: $" + jugador.getPrecio());

            btnComprar.setOnClickListener(v -> {
                Log.d("ADAPTER", "Botón comprar presionado para: " + jugador.getNombre());
                if (listener != null) {
                    listener.onComprarClick(jugador);
                }
            });
        }
    }
}
