package com.example.magosdelbalon;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Locale;

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
        ImageView imageJugador;
        Button btnAccion;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.textNombre);
            tvPosicion = itemView.findViewById(R.id.textPosicion);
            tvOverall = itemView.findViewById(R.id.textOverall);
            tvPrecio = itemView.findViewById(R.id.textPrecio);
            btnAccion = itemView.findViewById(R.id.btnComprar);
            imageJugador = itemView.findViewById(R.id.imageJugador);
        }

        public void bind(Jugador jugador) {
            tvNombre.setText(jugador.getNombre());
            tvPosicion.setText(jugador.getPosicion());
            tvOverall.setText("OVR: " + jugador.getOverall());

            String gsUrl = jugador.getImageUrl();

            FirebaseStorage.getInstance()
                    .getReferenceFromUrl(gsUrl)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(itemView.getContext())
                                .load(uri.toString())
                                .error(R.drawable.defaultplayer)
                                .into(imageJugador);
                    })
                    .addOnFailureListener(e -> {
                        imageJugador.setImageResource(R.drawable.defaultplayer);
                    });

            String precioFormateado = MainActivity.formatearDinero(jugador.getPrecio());
            tvPrecio.setText("Precio: " + precioFormateado);

            btnAccion.setText(modo == Modo.COMPRAR ? "Comprar" : "Vender");

            btnAccion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJugadorClick(jugador);
                }
            });
        }
    }
}
