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

    public enum Modo { COMPRAR, VENDER }

    private List<Jugador> listaJugadores;
    private OnJugadorClickListener listener;
    private Modo modo;

    public interface OnJugadorClickListener {
        void onJugadorClick(Jugador jugador);
    }

    public JugadorAdapter(List<Jugador> listaJugadores, OnJugadorClickListener listener, Modo modo) {
        this.listaJugadores = listaJugadores;
        this.listener = listener;
        this.modo = modo;
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
        Button btnAccion;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.textNombre);
            tvPosicion = itemView.findViewById(R.id.textPosicion);
            tvOverall = itemView.findViewById(R.id.textOverall);
            tvPrecio = itemView.findViewById(R.id.textPrecio);
            btnAccion = itemView.findViewById(R.id.btnComprar); // Mismo botón, pero renombramos lógica
        }

        public void bind(Jugador jugador) {
            tvNombre.setText(jugador.getNombre());
            tvPosicion.setText(jugador.getPosicion());
            tvOverall.setText("OVR: " + jugador.getOverall());
            tvPrecio.setText("Precio: " + jugador.getPrecio() + "€");

            if (modo == Modo.COMPRAR) {
                btnAccion.setText("Comprar");
            } else {
                btnAccion.setText("Vender");
            }

            btnAccion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJugadorClick(jugador);
                }
            });
        }
    }
}
