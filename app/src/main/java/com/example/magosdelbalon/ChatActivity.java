package com.example.magosdelbalon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magosdelbalon.mensaje.Mensaje;
import com.example.magosdelbalon.mensaje.MensajeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String miUid;
    private String uidDestino;
    private String chatId;
    private boolean primeraCarga = true;
    private EditText inputMensaje;
    private Button btnEnviar;
    private RecyclerView recyclerMensajes;

    private FireStoreHelper firestoreHelper;
    private List<Mensaje> listaMensajes;
    private MensajeAdapter mensajeAdapter;
    private ImageView imagePerfilAmigo;
    private TextView textUsernameAmigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.applyUserBrightness(this);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        miUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        uidDestino = getIntent().getStringExtra("uidDestino");

        if (miUid == null || uidDestino == null) {
            finish();
            return;
        }

        chatId = FireStoreHelper.getInstance().generarChatId(miUid, uidDestino);
        firestoreHelper = FireStoreHelper.getInstance();

        inputMensaje = findViewById(R.id.input_mensaje);
        btnEnviar = findViewById(R.id.btn_enviar);
        recyclerMensajes = findViewById(R.id.recycler_mensajes);
        imagePerfilAmigo = findViewById(R.id.image_perfil_amigo);
        textUsernameAmigo = findViewById(R.id.text_username_amigo);

        listaMensajes = new ArrayList<>();
        mensajeAdapter = new MensajeAdapter(listaMensajes, miUid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // Nuevos abajo
        layoutManager.setStackFromEnd(true);  // Empieza desde el final
        recyclerMensajes.setLayoutManager(layoutManager);
        recyclerMensajes.setAdapter(mensajeAdapter);

        firestoreHelper.escucharMensajes(chatId, (snapshots, e) -> {
            if (e != null) return;

            boolean hayNuevos = false;

            for (DocumentChange cambio : snapshots.getDocumentChanges()) {
                if (cambio.getType() == DocumentChange.Type.ADDED) {
                    Mensaje m = cambio.getDocument().toObject(Mensaje.class);
                    listaMensajes.add(0, m);
                    mensajeAdapter.notifyItemInserted(0);

                    if (!primeraCarga && m.getRemitenteId().equals(miUid)) {
                        recyclerMensajes.scrollToPosition(0);
                    }

                    hayNuevos = true;
                }
            }

            if (primeraCarga && hayNuevos) {
                recyclerMensajes.scrollToPosition(0);
                primeraCarga = false;
            }

            primeraCarga = false;
        });



        firestoreHelper.getUsuario(miUid, new FireStoreHelper.UsuarioCallback() {
            @Override
            public void onUsuarioObtenido(User usuario) {
                String remitenteNombre = usuario != null ? usuario.getUsername() : "Alguien";
                configurarBotonEnviar(remitenteNombre);
            }

            @Override
            public void onError() {
                configurarBotonEnviar("Alguien");
            }
        });
        cargarDatosAmigo(uidDestino);
    }
    private void cargarDatosAmigo(String uid) {
        firestoreHelper.getUsuario(uid, new FireStoreHelper.UsuarioCallback() {
            @Override
            public void onUsuarioObtenido(User usuario) {
                if (usuario != null) {
                    textUsernameAmigo.setText(usuario.getUsername());
                    firestoreHelper.getProfileImageForUser(uid, imagePerfilAmigo);
                } else {
                    textUsernameAmigo.setText("Amigo");
                    imagePerfilAmigo.setImageResource(R.drawable.ic_profile_placeholder);
                }
            }

            @Override
            public void onError() {
                textUsernameAmigo.setText("Amigo");
                imagePerfilAmigo.setImageResource(R.drawable.ic_profile_placeholder);
            }
        });
    }

    private void configurarBotonEnviar(String remitenteNombre) {
        btnEnviar.setOnClickListener(v -> {
            String texto = inputMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                Mensaje mensaje = new Mensaje(miUid, texto, System.currentTimeMillis());

                Map<String, Object> mensajeMap = new HashMap<>();
                mensajeMap.put("remitenteId", mensaje.getRemitenteId());
                mensajeMap.put("contenido", mensaje.getContenido());
                mensajeMap.put("timestamp", mensaje.getTimestamp());
                mensajeMap.put("destinatarioId", uidDestino);
                mensajeMap.put("remitenteNombre", remitenteNombre);

                firestoreHelper.enviarMensajeMap(chatId, mensajeMap);
                inputMensaje.setText("");
                recyclerMensajes.scrollToPosition(0);
            }
        });
    }
}
