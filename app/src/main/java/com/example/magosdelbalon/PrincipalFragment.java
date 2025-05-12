package com.example.magosdelbalon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PrincipalFragment extends Fragment {

    private FireStoreHelper fireStoreHelper = new FireStoreHelper();
    private double mediaEquipo;
    private double mediaRival;

    public PrincipalFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PrincipalFragment", "Inflando layout del fragment");

        View rootView = inflater.inflate(R.layout.fragment_principal, container, false);

        // Obtener argumentos
        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        Log.d("PrincipalFragment", "Liga recibida en fragment: " + ligaName);

        if (!TextUtils.isEmpty(ligaName)) {
            cargarDatosLiga(rootView, ligaName);
        }

        // Configurar el botón para iniciar el partido
        Button buttonStartMatch = rootView.findViewById(R.id.button_start_match);
        buttonStartMatch.setOnClickListener(v -> {
            simularPartido(mediaEquipo, mediaRival);
        });

        return rootView;
    }

    private void cargarDatosLiga(View rootView, String ligaName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences prefs = requireContext().getSharedPreferences("MagosPrefs", Context.MODE_PRIVATE);
        boolean mediaVisible = prefs.getBoolean("mediaVisible_" + ligaName, false);

        TextView mediaTextView = rootView.findViewById(R.id.text_view_home_team_rating);
        TextView teamTextView = rootView.findViewById(R.id.text_view_home_team_name);
        TextView rivalTextView = rootView.findViewById(R.id.text_view_away_team_name);
        TextView rivalMediaTextView = rootView.findViewById(R.id.text_view_away_team_rating);  // TextView para la media del rival
        TextView alertaAlineacion = rootView.findViewById(R.id.text_view_alerta_alineacion);
        ImageButton botonLupa = rootView.findViewById(R.id.button_show_rival_media);

        // Si ya se vio la media, ocultamos la lupa y mostramos la media directamente
        if (mediaVisible) {
            botonLupa.setVisibility(View.GONE);
            rivalMediaTextView.setVisibility(View.VISIBLE);
        } else {
            botonLupa.setVisibility(View.VISIBLE);
            rivalMediaTextView.setVisibility(View.GONE);
        }
        // Lógica al pulsar la lupa
        botonLupa.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Inspeccionar rival")
                    .setMessage("¿Quieres pagar 1 millón para ver la media del rival?")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        fireStoreHelper.descontarMillonPorInspeccion(ligaName, new FireStoreHelper.InspeccionCallback() {
                            @Override
                            public void onSuccess(String message) {
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).refrescarDatosLiga();
                                }
                                rivalMediaTextView.setVisibility(View.VISIBLE);
                                botonLupa.setVisibility(View.GONE);
                                prefs.edit().putBoolean("mediaVisible_" + ligaName, true).apply();
                            }

                            @Override
                            public void onFailure(String error) {
                                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        .setTitle("Error")
                                        .setMessage(error)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        Map<String, TextView> textViewsAlineacion = new HashMap<>();
        fireStoreHelper.cargarAlineacion(userId, ligaName, textViewsAlineacion);

        fireStoreHelper.verificarAlineacionCompleta(userId, ligaName, new FireStoreHelper.AlineacionCallback() {
            @Override
            public void onCheckCompleted(boolean alineacionCompleta) {
                if (!alineacionCompleta) {
                    alertaAlineacion.setVisibility(View.VISIBLE);
                } else {
                    alertaAlineacion.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                alertaAlineacion.setText("Error al cargar alineación");
                alertaAlineacion.setVisibility(View.VISIBLE);
                Log.e("PrincipalFragment", error);
            }
        });

        fireStoreHelper.obtenerDatosLigaPorId(ligaName, new FireStoreHelper.FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                actualizarEquipo(ligaData, teamTextView);
                calcularMediaEquipo(userId, ligaName, mediaTextView);
                actualizarRivalPendiente(ligaData, rivalTextView);
                // Calcular la media del equipo rival
                calcularMediaRival(ligaData, rivalMediaTextView);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PrincipalFragment", "Error al obtener datos de liga: " + errorMessage);
                teamTextView.setText("Error al cargar equipo");
            }
        });


         mediaEquipo = getMediaFromTextView(mediaTextView);
         mediaRival = getMediaFromTextView(rivalMediaTextView);
    }


    private void actualizarEquipo(Map<String, Object> ligaData, TextView teamTextView) {
        if (ligaData != null && ligaData.containsKey("equipo")) {
            String equipo = (String) ligaData.get("equipo");
            teamTextView.setText(equipo);
        } else {
            teamTextView.setText("Desconocido");
        }
    }
    private void calcularMediaRival(Map<String, Object> ligaData, TextView rivalMediaTextView) {
        if (ligaData.containsKey("progresoLiga")) {
            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");

            if (progresoLiga.containsKey("pendientesJugar")) {
                List<String> pendientes = (List<String>) progresoLiga.get("pendientesJugar");

                if (!pendientes.isEmpty()) {
                    String proximoRival = pendientes.get(0);

                    // Construye el nombre de la función a partir del rival (opcional, si usas nombres dinámicos)
                    fireStoreHelper.fetchRivalAverageByTeamName(proximoRival, new FireStoreHelper.AverageCallback() {
                        @Override
                        public void onAverageLoaded(double average) {
                            rivalMediaTextView.setText("Media: " + Math.round(average));
                        }

                        @Override
                        public void onError(String error) {
                            rivalMediaTextView.setText("Error");
                            Log.e("PrincipalFragment", "Error obteniendo media rival: " + error);
                        }
                    });


                } else {
                    rivalMediaTextView.setText("No quedan rivales");
                }
            } else {
                rivalMediaTextView.setText("Sin rivales pendientes");
            }
        } else {
            rivalMediaTextView.setText("Progreso no disponible");
        }
    }

    private void calcularMediaEquipo(String userId, String ligaName, TextView mediaTextView) {
        fireStoreHelper.calcularMediaEquipo(userId, ligaName, mediaTextView);
    }

    private void actualizarRivalPendiente(Map<String, Object> ligaData, TextView rivalTextView) {
        if (ligaData.containsKey("progresoLiga")) {
            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
            if (progresoLiga.containsKey("pendientesJugar")) {
                try {
                    Object pendientesObj = progresoLiga.get("pendientesJugar");
                    if (pendientesObj instanceof java.util.List) {
                        java.util.List<String> pendientes = (java.util.List<String>) pendientesObj;
                        if (!pendientes.isEmpty()) {
                            String proximoRival = pendientes.get(0);  // Primer equipo pendiente
                            rivalTextView.setText(proximoRival);
                        } else {
                            rivalTextView.setText("No quedan rivales");
                        }
                    }
                } catch (Exception e) {
                    Log.e("PrincipalFragment", "Error al obtener rival: " + e.getMessage());
                    rivalTextView.setText("Error al obtener rival");
                }
            } else {
                rivalTextView.setText("Sin rivales pendientes");
            }
        } else {
            rivalTextView.setText("Progreso no disponible");
        }
    }

    private int getMediaFromTextView(TextView textView) {
        try {
            String mediaText = textView.getText().toString().replace("Media: ", "").trim();
            return Integer.parseInt(mediaText);
        } catch (NumberFormatException e) {
            Log.e("PrincipalFragment", "Error al obtener media: " + e.getMessage());
            return 0;
        }
    }

    private void simularPartido(double mediaEquipo, double mediaRival) {
        // Calcular la probabilidad de victoria
        double probabilidadVictoria = calcularProbabilidadVictoria(mediaEquipo, mediaRival);

        // Simular el resultado del partido
        Random random = new Random();
        double resultado = random.nextDouble(); // Genera un número aleatorio entre 0 y 1

        TextView matchResultTextView = getView().findViewById(R.id.text_view_match_result);
        if (resultado < probabilidadVictoria) {
            matchResultTextView.setText("¡Tu equipo ha ganado el partido!");
            Log.d("PrincipalFragment", "¡Tu equipo ha ganado el partido!");
        } else {
            matchResultTextView.setText("Tu equipo ha perdido el partido.");
            Log.d("PrincipalFragment", "Tu equipo ha perdido el partido.");
        }
    }

    private double calcularProbabilidadVictoria(double mediaEquipo, double mediaRival) {
        // Fórmula simple para calcular la probabilidad de victoria
        // Puedes ajustar esta fórmula según tus necesidades
        double diferencia = mediaEquipo - mediaRival;
        double probabilidadBase = 0.5; // Probabilidad base del 50%

        // Ajustar la probabilidad en función de la diferencia de medias
        double ajuste = diferencia * 0.05; // Ajuste del 5% por cada punto de diferencia
        double probabilidadVictoria = probabilidadBase + ajuste;

        // Asegurarse de que la probabilidad esté entre 0 y 1
        probabilidadVictoria = Math.max(0, Math.min(1, probabilidadVictoria));

        return probabilidadVictoria;
    }


}
