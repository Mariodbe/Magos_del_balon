package com.example.magosdelbalon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class PlayerSelectionDialog {

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(String player);
    }

    public static void show(Context context, List<String> players, OnPlayerSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_player_selection, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.listViewPlayers);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, players);
        listView.setAdapter(adapter);

        builder.setTitle("Selecciona un jugador");
        builder.setNegativeButton("Cancelar", null);

        final AlertDialog dialog = builder.create();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlayer = players.get(position);
            listener.onPlayerSelected(selectedPlayer);
            dialog.dismiss();
        });

        dialog.show();
    }
}
