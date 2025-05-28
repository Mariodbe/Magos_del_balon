package com.example.magosdelbalon.mensaje;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.R;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private List<Mensaje> mensajes;
    private String miUid;

    public MensajeAdapter(List<Mensaje> mensajes, String miUid) {
        this.mensajes = mensajes;
        this.miUid = miUid;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 0) ? R.layout.item_mensaje_propio : R.layout.item_mensaje_ajeno;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        holder.txtMensaje.setText(mensajes.get(position).getContenido());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).getRemitenteId().equals(miUid) ? 0 : 1;
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txt_mensaje);
        }
    }
}
