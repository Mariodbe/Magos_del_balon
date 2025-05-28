package com.example.magosdelbalon.amigos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    public interface OnAmigoClickListener {
        void onAmigoClick(String uid);
    }

    private List<String> listaUids;
    private OnAmigoClickListener listener;

    public AmigosAdapter(List<String> listaUids, OnAmigoClickListener listener) {
        this.listaUids = listaUids;
        this.listener = listener;
    }

    @Override
    public AmigoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AmigoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AmigoViewHolder holder, int position) {
        String uid = listaUids.get(position);
        holder.bind(uid);
    }

    @Override
    public int getItemCount() {
        return listaUids.size();
    }

    class AmigoViewHolder extends RecyclerView.ViewHolder {
        TextView txtUid;

        public AmigoViewHolder(View itemView) {
            super(itemView);
            txtUid = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAmigoClick(listaUids.get(pos));
                }
            });
        }

        void bind(String uid) {
            txtUid.setText(uid); // Aquí podrías cargar el nombre con Firestore si quieres
        }
    }
}
