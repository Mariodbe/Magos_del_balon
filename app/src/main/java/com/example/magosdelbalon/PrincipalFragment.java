package com.example.magosdelbalon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Map;

public class PrincipalFragment extends Fragment {

    private TextView ligaNombreTextView;
    private TextView equipoTextView;
    private TextView dineroInicialTextView;
    private ImageButton btnBackToHome;

    public PrincipalFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PrincipalFragment", "Inflando layout del fragment");

        View rootView = inflater.inflate(R.layout.fragment_principal, container, false);

        // Inicialización de vistas
        ligaNombreTextView = rootView.findViewById(R.id.leagueNameTextView);
        equipoTextView = rootView.findViewById(R.id.teamNameTextView);
        dineroInicialTextView = rootView.findViewById(R.id.dineroInicialTextView);
        btnBackToHome = rootView.findViewById(R.id.btn_back_to_home);

        Log.d("PrincipalFragment", "leagueNameTextView es null? " + (ligaNombreTextView == null));
        Log.d("PrincipalFragment", "teamNameTextView es null? " + (equipoTextView == null));
        Log.d("PrincipalFragment", "btnBackToHome es null? " + (btnBackToHome == null));

        // Obtener argumentos
        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        Log.d("PrincipalFragment", "Liga recibida en fragment: " + ligaName);

        if (ligaName != null) {
            obtenerDatosLiga(ligaName);
        } else {
            Log.e("PrincipalFragment", "ligaName es null, no se pudo continuar");
        }

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("PrincipalFragment", "Botón Home presionado, navegando a HomeActivity");
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(0, 0);
                    }
                }
            });
        }

        return rootView;
    }

    private void obtenerDatosLiga(String ligaName) {
        FireStoreHelper helper = new FireStoreHelper();

        Log.d("PrincipalFragment", "Llamando a obtenerDatosLigaPorId para: " + ligaName);

        helper.obtenerDatosLigaPorId(ligaName, new FireStoreHelper.FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                Log.d("PrincipalFragment", "Datos obtenidos: " + ligaData);

                String equipo = (String) ligaData.get("equipo");
                Log.d("PrincipalFragment", "Equipo recibido: " + equipo);

                Object dineroInicialObject = ligaData.get("dinero");
                String dineroInicialText = null;
                if (dineroInicialObject instanceof Number) {
                    dineroInicialText = String.valueOf(((Number) dineroInicialObject).intValue());

                }

                if (isAdded()) {
                    if(dineroInicialText == null){
                        dineroInicialText = "N/A";
                    }
                    if (ligaNombreTextView != null && equipoTextView != null && dineroInicialTextView != null) {
                        ligaNombreTextView.setText("Liga: " + ligaName);
                        equipoTextView.setText("Equipo: " + equipo);

                        if (!TextUtils.isEmpty(dineroInicialText)) {
                            dineroInicialTextView.setText("Dinero inicial: " + dineroInicialText);
                        } else {
                            dineroInicialTextView.setText("Dinero inicial: N/A");
                        }


                        Log.d("PrincipalFragment", "TextViews actualizados correctamente");
                    } else {
                        Log.e("PrincipalFragment", "TextViews son null al intentar actualizar");
                    }
                } else {
                    Log.e("PrincipalFragment", "Fragmento no está agregado al activity (isAdded = false)");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PrincipalFragment", "Error en Firestore: " + errorMessage);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
