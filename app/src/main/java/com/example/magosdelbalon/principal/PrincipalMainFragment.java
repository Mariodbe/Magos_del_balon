package com.example.magosdelbalon.principal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magosdelbalon.MainActivity;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class PrincipalMainFragment extends Fragment {

    private ViewPager2 viewPager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PrincipalMainFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal_main, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new PrincipalPagerAdapter(this, getArguments()));

        // Llama al método para cargar datos cuando se crea la vista
        cargarDatosDesdeFirestore();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // También puedes llamar al método para cargar datos cuando el fragmento se reanuda
        cargarDatosDesdeFirestore();
    }

    private void cargarDatosDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("PrincipalMainFragment", "Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null) {
                    // Obtén el nombre de la liga actual desde los argumentos del fragmento
                    Bundle args = getArguments();
                    String ligaName = args != null ? args.getString("leagueName") : null;

                    if (ligaName != null && userData.containsKey(ligaName)) {
                        Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaName);
                        Log.d("PrincipalMainFragment", "Datos de la liga '" + ligaName + "': " + ligaData.toString());

                        if (ligaData.containsKey("dinero")) {
                            long dinero = ((Number) ligaData.get("dinero")).longValue();
                            Log.d("PrincipalMainFragment", "Dinero cargado desde Firestore: " + dinero);
                            // Actualiza la interfaz de usuario con el valor del dinero
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).actualizarDinero((int) dinero);
                            }
                        } else {
                            Log.e("PrincipalMainFragment", "Campo 'dinero' no encontrado en la liga '" + ligaName + "'");
                        }
                    } else {
                        Log.e("PrincipalMainFragment", "Liga '" + ligaName + "' no encontrada en los datos del usuario");
                    }
                } else {
                    Log.e("PrincipalMainFragment", "Datos del usuario son nulos");
                }
            } else {
                Log.e("PrincipalMainFragment", "Documento del usuario no existe");
            }
        }).addOnFailureListener(e -> {
            Log.e("PrincipalMainFragment", "Error al cargar datos: " + e.getMessage());
        });
    }
}

