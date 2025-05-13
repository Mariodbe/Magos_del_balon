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
    private int nivelCentroMedico;
    private int nivelCiudadDeportiva;
    private int nivelEstadio;

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
        TextView rivalMediaTextView = rootView.findViewById(R.id.text_view_away_team_rating);
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

        // Llamar al método para obtener los niveles del estadio
        // Llamar al método para obtener los niveles del estadio
        fireStoreHelper.getEstadioData(userId, ligaName, new FireStoreHelper.EstadioCallback() {
            @Override
            public void onEstadioDataLoaded(int centroMedico, int ciudadDeportiva, int estadio) {
                nivelCentroMedico = centroMedico;
                nivelCiudadDeportiva = ciudadDeportiva;
                nivelEstadio = estadio;

                Log.d("PrincipalFragment", "Centro Médico: " + nivelCentroMedico);
                Log.d("PrincipalFragment", "Ciudad Deportiva: " + nivelCiudadDeportiva);
                Log.d("PrincipalFragment", "Estadio: " + nivelEstadio);
            }

            @Override
            public void onError(String error) {
                Log.e("PrincipalFragment", "Error al obtener datos del estadio: " + error);
            }
        });

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
                            if (Double.isNaN(average) || Double.isInfinite(average)) {
                                Log.e("PrincipalFragment", "El valor de la media no es válido: " + average);
                                average = 0; // o algún valor por defecto que tenga sentido en tu contexto
                            }
                            rivalMediaTextView.setText("Media: " + Math.round(average));
                            mediaRival = getMediaFromTextView(rivalMediaTextView);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("PrincipalFragment", "Error obteniendo media rival: " + error);
                            rivalMediaTextView.setText("Media: 0"); // Establecer un valor predeterminado en caso de error
                        }
                    });

                } else {
                    rivalMediaTextView.setText("Media: 0"); // Establecer un valor predeterminado si no hay rivales pendientes
                }
            } else {
                rivalMediaTextView.setText("Media: 0"); // Establecer un valor predeterminado si no hay rivales pendientes
            }
        } else {
            rivalMediaTextView.setText("Media: 0"); // Establecer un valor predeterminado si el progreso de la liga no está disponible
        }
    }


    private void calcularMediaEquipo(String userId, String ligaName, TextView mediaTextView) {
        fireStoreHelper.calcularMediaEquipo(userId, ligaName, new FireStoreHelper.MediaCallback() {
            @Override
            public void onMediaCalculated(int media) {
                mediaTextView.setText("Media: " + media);
                mediaEquipo = media;  // Actualizamos la variable `mediaEquipo` solo después de que se establece el texto
            }

            @Override
            public void onError(String error) {
                Log.e("PrincipalFragment", "Error al calcular media del equipo: " + error);
                mediaTextView.setText("Media: 0");
                mediaEquipo = 0;
            }
        });
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
            String mediaText = textView.getText().toString().trim();
            if (mediaText.isEmpty() || !mediaText.startsWith("Media: ")) {
                Log.e("PrincipalFragment", "El texto de la media no está en el formato esperado: " + mediaText);
                return 0; // o algún valor por defecto que tenga sentido en tu contexto
            }
            String mediaValue = mediaText.substring("Media: ".length()).trim();
            if (mediaValue.isEmpty()) {
                Log.e("PrincipalFragment", "El valor de la media está vacío.");
                return 0; // o algún valor por defecto que tenga sentido en tu contexto
            }
            return Integer.parseInt(mediaValue);
        } catch (NumberFormatException e) {
            Log.e("PrincipalFragment", "Error al obtener media: " + e.getMessage());
            return 0; // o algún valor por defecto que tenga sentido en tu contexto
        }


    }




    private void simularPartido(double mediaEquipo, double mediaRival) {
        // Calcular la probabilidad de victoria
        double probabilidadVictoria = calcularProbabilidadVictoria(mediaEquipo, mediaRival);

        // Convertir a porcentaje y redondear
        int porcentajeVictoria = (int) Math.round(probabilidadVictoria * 100);

        // Simular el resultado del partido
        Random random = new Random();
        double resultado = random.nextDouble(); // Genera un número aleatorio entre 0 y 1

        // Aquí deberías tener una referencia a tu TextView y contexto adecuado
        TextView matchResultTextView = getView().findViewById(R.id.text_view_match_result);

        if (resultado < probabilidadVictoria) {
             matchResultTextView.setText("¡Has ganado el partido! Porcentaje de victoria: " + porcentajeVictoria + "%");
            System.out.println("¡Has ganado el partido! Porcentaje de victoria: " + porcentajeVictoria + "%");
        } else {
             matchResultTextView.setText("Has perdido el partido. Porcentaje de victoria: " + porcentajeVictoria + "%");
            System.out.println("Has perdido el partido. Porcentaje de victoria: " + porcentajeVictoria + "%");
        }
    }

    private double calcularProbabilidadVictoria(double mediaEquipo, double mediaRival) {
        double probabilidadBase = 0.5; // 50% base

        // Generar niveles aleatorios para el rival (1 a 10)
        Random random = new Random();
        int nivelCentroMedicoRival = random.nextInt(10) + 1;
        int nivelCiudadDeportivaRival = random.nextInt(10) + 1;
        int nivelEstadioRival = random.nextInt(10) + 1;

        Log.d("PrincipalFragment", "Niveles rival - Centro Médico: " + nivelCentroMedicoRival +
                ", Ciudad Deportiva: " + nivelCiudadDeportivaRival +
                ", Estadio: " + nivelEstadioRival);

        Log.d("PrincipalFragment", "Media equipo: " + mediaEquipo);
        Log.d("PrincipalFragment", "Media rival: " + mediaRival);

        // Ajuste por media
        double diferenciaMedia = mediaEquipo - mediaRival;
        double ajusteMedia = diferenciaMedia * 0.02; // 2% por punto de diferencia

        // Suma de niveles del usuario y del rival
        int totalNivelesUsuario = nivelCentroMedico + nivelCiudadDeportiva + nivelEstadio;
        int totalNivelesRival = nivelCentroMedicoRival + nivelCiudadDeportivaRival + nivelEstadioRival;

        Log.d("PrincipalFragment", "Niveles usuario - Total: " + totalNivelesUsuario +
                ", Niveles rival - Total: " + totalNivelesRival);

        // Ajuste por instalaciones
        int diferenciaNiveles = totalNivelesUsuario - totalNivelesRival;
        double ajusteInstalaciones = diferenciaNiveles * 0.01; // 1% por punto de diferencia

        // Sumar los ajustes
        double probabilidadVictoria = probabilidadBase + ajusteMedia + ajusteInstalaciones;

        // Limitar la probabilidad entre 10% y 90%
        probabilidadVictoria = Math.max(0.1, Math.min(0.9, probabilidadVictoria));

        Log.d("PrincipalFragment", "Probabilidad de victoria calculada: " + (probabilidadVictoria * 100) + "%");

        return probabilidadVictoria;
    }

}
