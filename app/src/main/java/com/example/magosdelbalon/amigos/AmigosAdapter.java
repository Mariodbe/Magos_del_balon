package com.example.magosdelbalon.amigos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.R;
import com.example.magosdelbalon.User;

import java.util.List;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private List<User> listaAmigos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(String uid);
    }

    public AmigosAdapter(List<User> listaAmigos, OnItemClickListener listener) {
        this.listaAmigos = listaAmigos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        User amigo = listaAmigos.get(position);
        holder.usernameTextView.setText(amigo.getUsername());
        FireStoreHelper fireStoreHelper = new FireStoreHelper();
        // Carga la imagen usando el método getProfileImageForUser
        fireStoreHelper.getProfileImageForUser(amigo.getUid(), holder.imagePerfilAmigo);

        holder.itemView.setOnClickListener(v -> listener.onClick(amigo.getUid()));
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public void setListaAmigos(List<User> nuevaLista) {
        listaAmigos.clear();
        listaAmigos.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class AmigoViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView imagePerfilAmigo;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.text_username);
            imagePerfilAmigo = itemView.findViewById(R.id.image_perfil_amigo);
        }
    }
}
