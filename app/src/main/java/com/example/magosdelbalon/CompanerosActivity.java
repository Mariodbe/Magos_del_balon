package com.example.magosdelbalon;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CompanerosActivity extends AppCompatActivity {

    private EditText editSearch;
    private Button btnSeguir, btnSiguiendo, btnSeguidores;
    private RecyclerView recyclerUsuarios;
    private ImageButton btnBack;

    private List<User> usuariosParaSeguir;
    private List<User> usuariosSiguiendo;
    private List<User> usuariosSeguidores;

    private UserAdapter adapter;

    private String currentUserId = "";
    private String currentUsername = "";
    private int pestañaActual = 0;

    private FireStoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.applyUserBrightness(this);
        setContentView(R.layout.activity_companeros);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        editSearch = findViewById(R.id.edit_search);
        btnSeguir = findViewById(R.id.btn_seguir);
        btnSiguiendo = findViewById(R.id.btn_siguiendo);
        btnSeguidores = findViewById(R.id.btn_seguidores);
        recyclerUsuarios = findViewById(R.id.recycler_usuarios);
        btnBack = findViewById(R.id.btn_back);

        firestoreHelper = new FireStoreHelper();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        usuariosParaSeguir = new ArrayList<>();
        usuariosSiguiendo = new ArrayList<>();
        usuariosSeguidores = new ArrayList<>();

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSeguir.setOnClickListener(v -> {
            pestañaActual = 0;
            actualizarColoresPestañas();
            actualizarListaUsuarios();
        });

        btnSiguiendo.setOnClickListener(v -> {
            pestañaActual = 1;
            actualizarColoresPestañas();
            actualizarListaUsuarios();
        });

        btnSeguidores.setOnClickListener(v -> {
            pestañaActual = 2;
            actualizarColoresPestañas();
            actualizarListaUsuarios();
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (adapter != null) adapter.filter(s.toString());
            }
        });

        firestoreHelper.fetchUsername(currentUserId, new FireStoreHelper.UsernameCallback() {
            @Override
            public void onUsernameFetched(String username) {
                currentUsername = username;
                cargarListasDesdeFirestore();
            }

            @Override
            public void onError() {
                currentUsername = "Usuario";
                Toast.makeText(CompanerosActivity.this, "Error al obtener username", Toast.LENGTH_SHORT).show();
            }
        });

        actualizarColoresPestañas();
    }

    private void actualizarColoresPestañas() {
        btnSeguir.setBackgroundTintList(getColorStateList(pestañaActual == 0 ? android.R.color.holo_orange_light : android.R.color.darker_gray));
        btnSiguiendo.setBackgroundTintList(getColorStateList(pestañaActual == 1 ? android.R.color.holo_orange_light : android.R.color.darker_gray));
        btnSeguidores.setBackgroundTintList(getColorStateList(pestañaActual == 2 ? android.R.color.holo_orange_light : android.R.color.darker_gray));
    }

    private void actualizarListaUsuarios() {
        List<User> mostrar;
        if (pestañaActual == 0) {
            mostrar = usuariosParaSeguir;
        } else if (pestañaActual == 1) {
            mostrar = usuariosSiguiendo;
        } else {
            mostrar = usuariosSeguidores;
        }

        adapter = new UserAdapter(this, mostrar, currentUsername, pestañaActual, (user, isFollowing) -> {
            if (pestañaActual == 2) {
                firestoreHelper.eliminarSeguidor(this, currentUserId, user.getUid(), this::cargarListasDesdeFirestore);
            } else if (isFollowing) {
                firestoreHelper.dejarDeSeguir(this, currentUserId, user.getUid(), this::cargarListasDesdeFirestore);
            } else {
                firestoreHelper.seguir(this, currentUserId, user.getUid(), this::cargarListasDesdeFirestore);
            }
        });

        recyclerUsuarios.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void cargarListasDesdeFirestore() {
        firestoreHelper.cargarListasUsuarios(currentUserId, new FireStoreHelper.UserListsCallback() {
            @Override
            public void onListsFetched(List<User> paraSeguir, List<User> siguiendo, List<User> seguidores) {
                usuariosParaSeguir = paraSeguir;
                usuariosSiguiendo = siguiendo;
                usuariosSeguidores = seguidores;
                runOnUiThread(CompanerosActivity.this::actualizarListaUsuarios);
            }

            @Override
            public void onError() {
                Toast.makeText(CompanerosActivity.this, "Error al cargar listas de usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
