package com.example.magosdelbalon.amigos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.ChatActivity;
import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ListaAmigosActivity extends AppCompatActivity {

    private String miUid;
    private RecyclerView recyclerAmigos;
    private AmigosAdapter amigosAdapter;
    private List<String> listaUidsAmigos;
    private FireStoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);
        // Ocultar la barra de acción (ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        miUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (miUid == null) {
            finish();
            return;
        }

        recyclerAmigos = findViewById(R.id.recycler_amigos);
        listaUidsAmigos = new ArrayList<>();
        amigosAdapter = new AmigosAdapter(listaUidsAmigos, uid -> {
            // Cuando seleccionen un amigo, abrir ChatActivity con ese uidDestino
            Intent intent = new Intent(ListaAmigosActivity.this, ChatActivity.class);
            intent.putExtra("uidDestino", uid);
            startActivity(intent);
        });
        recyclerAmigos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAmigos.setAdapter(amigosAdapter);

        firestoreHelper = FireStoreHelper.getInstance();

        // Cargar lista de amigos
        firestoreHelper.getSeguidoresMutuos(miUid, new FireStoreHelper.ListaCallback() {
            @Override
            public void onListaObtenida(List<String> lista) {
                listaUidsAmigos.clear();
                listaUidsAmigos.addAll(lista);
                amigosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
            }
        });

    }
}
