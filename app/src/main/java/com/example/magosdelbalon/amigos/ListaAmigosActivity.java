package com.example.magosdelbalon.amigos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.ChatActivity;
import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.R;
import com.example.magosdelbalon.User;
import com.example.magosdelbalon.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ListaAmigosActivity extends AppCompatActivity {

    private String miUid;
    private RecyclerView recyclerAmigos;
    private AmigosAdapter amigosAdapter;
    private List<User> listaAmigos;
    private FireStoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.applyUserBrightness(this);
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
        ImageButton btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> finish());

        ImageButton btnHelp = findViewById(R.id.btn_help);
        btnHelp.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(ListaAmigosActivity.this)
                    .setTitle("¿Cómo funciona?")
                    .setMessage("Solo puedes chatear con personas que te siguen y que tú también sigues.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });


        recyclerAmigos = findViewById(R.id.recycler_amigos);
        listaAmigos = new ArrayList<>();
        amigosAdapter = new AmigosAdapter(listaAmigos, uid -> {
            // Cuando seleccionen un amigo, abrir ChatActivity con ese uidDestino
            Intent intent = new Intent(ListaAmigosActivity.this, ChatActivity.class);
            intent.putExtra("uidDestino", uid);
            startActivity(intent);
        });
        recyclerAmigos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAmigos.setAdapter(amigosAdapter);

        firestoreHelper = FireStoreHelper.getInstance();

        firestoreHelper.getSeguidoresMutuos(miUid, new FireStoreHelper.ListaCallback() {
            @Override
            public void onListaObtenida(List<String> listaUids) {
                List<User> listaUsuarios = new ArrayList<>();
                // Contador para saber cuándo terminan las consultas
                AtomicInteger pendientes = new AtomicInteger(listaUids.size());

                for (String uidAmigo : listaUids) {
                    firestoreHelper.getUsuario(uidAmigo, new FireStoreHelper.UsuarioCallback() {
                        @Override
                        public void onUsuarioObtenido(User usuario) {
                            listaUsuarios.add(usuario);
                            if (pendientes.decrementAndGet() == 0) {
                                // Actualizar adapter en el hilo principal
                                runOnUiThread(() -> {
                                    amigosAdapter.setListaAmigos(listaUsuarios);
                                    amigosAdapter.notifyDataSetChanged();
                                });
                            }
                        }

                        @Override
                        public void onError() {
                            if (pendientes.decrementAndGet() == 0) {
                                runOnUiThread(() -> {
                                    amigosAdapter.setListaAmigos(listaUsuarios);
                                    amigosAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onError() {
                // manejar error
            }
        });


    }
}
