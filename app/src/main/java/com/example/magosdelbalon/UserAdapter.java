package com.example.magosdelbalon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private List<User> filteredUsers;
    private Context context;
    OnFollowClickListener followClickListener;
    private String currentUsername; // Usuario actual para comparar
    private int pestañaActual;

    public interface OnFollowClickListener {
        void onFollowClick(User user, boolean isFollowing);
    }
    public UserAdapter(Context context, List<User> users, String currentUsername, int pestañaActual, OnFollowClickListener listener) {
        this.context = context;
        this.users = users;
        this.filteredUsers = new ArrayList<>(users);
        this.currentUsername = currentUsername;
        this.followClickListener = listener;
        this.pestañaActual = pestañaActual;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = filteredUsers.get(position);
        holder.txtUsername.setText(user.getUsername());

        // Ocultar botón si es el propio usuario
        if (user.getUsername().equals(currentUsername)) {
            holder.btnFollow.setVisibility(View.GONE);
            return;
        }

        switch (pestañaActual) {
            case 0: // Seguir
                holder.btnFollow.setVisibility(View.VISIBLE);
                holder.btnFollow.setText("Seguir");
                holder.btnFollow.setOnClickListener(v -> {
                    if (followClickListener != null) {
                        followClickListener.onFollowClick(user, false);
                    }
                });
                break;

            case 1: // Siguiendo
                holder.btnFollow.setVisibility(View.VISIBLE);
                holder.btnFollow.setText("Dejar de seguir");
                holder.btnFollow.setOnClickListener(v -> {
                    if (followClickListener != null) {
                        followClickListener.onFollowClick(user, true);
                    }
                });
                break;

            case 2: // Seguidores
                holder.btnFollow.setVisibility(View.VISIBLE);
                holder.btnFollow.setText("Eliminar seguidor");
                holder.btnFollow.setOnClickListener(v -> {
                    if (followClickListener != null) {
                        followClickListener.onFollowClick(user, true);
                    }
                });
                break;

        }
    }


    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }

    public void filter(String text) {
        filteredUsers.clear();
        if (text.isEmpty()) {
            filteredUsers.addAll(users);
        } else {
            text = text.toLowerCase();
            for (User user : users) {
                if (user.getUsername().toLowerCase().contains(text)) {
                    filteredUsers.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean checkIfFollowing(String username) {
        // Aquí debes implementar la lógica para saber si el usuario actual sigue a "username"
        // Puede ser una lista local o una consulta Firebase
        // Por ahora devolvemos false para que lo completes
        return false;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername;
        Button btnFollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_username);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
