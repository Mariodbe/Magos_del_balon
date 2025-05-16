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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
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
    private int intensidadAgresividad;
    private int intensidadContraataques;
    private int intensidadPosesion;
    private int intensidadPresion;

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

        SharedPreferences prefs = requireContext().getSharedPreferences("MagosPrefs", Context.MODE_PRIVATE);
        String claveArbitro = "arbitroPermisividad_" + ligaName;
        int permisividadArbitro = prefs.getInt(claveArbitro, -1);

        // Si no hay valor guardado, generamos uno y lo guardamos
        if (permisividadArbitro == -1) {
            permisividadArbitro = new Random().nextInt(5) + 1;
            prefs.edit().putInt(claveArbitro, permisividadArbitro).apply();
        }
        TextView textViewPermisividad = rootView.findViewById(R.id.text_view_arbitro_permisividad);
        textViewPermisividad.setText("Permisividad árbitro: " + permisividadArbitro);

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
                actualizarRivalPendiente(ligaData, rivalTextView, getView().findViewById(R.id.layout_contenido_liga),
                        getView().findViewById(R.id.text_view_liga_finalizada));
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


        fireStoreHelper.getTacticaData(userId, ligaName, new FireStoreHelper.TacticasCallback() {
            @Override
            public void onTacticasDataLoaded(int agresividad, int contraataques, int posesion, int presion) {
                intensidadAgresividad=agresividad;
                intensidadContraataques=contraataques;
                intensidadPosesion=posesion;
                intensidadPresion=presion;


                Log.d("PrincipalFragment", "agresividad: " + intensidadAgresividad);
                Log.d("PrincipalFragment", "contraataques: " + intensidadContraataques);
                Log.d("PrincipalFragment", "posesion: " + intensidadPosesion);
                Log.d("PrincipalFragment", "presion: " + intensidadPresion);
            }

            @Override
            public void onError(String error) {
                Log.e("PrincipalFragment", "Error al obtener datos de las tacticas: " + error);
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
                Object pendientesObj = progresoLiga.get("pendientesJugar");
                if (pendientesObj instanceof Map) {
                    Map<String, Object> pendientesMap = (Map<String, Object>) pendientesObj;

                    if (!pendientesMap.isEmpty()) {
                        String proximoRival = pendientesMap.keySet().iterator().next();

                        fireStoreHelper.fetchRivalAverageByTeamName(proximoRival, new FireStoreHelper.AverageCallback() {
                            @Override
                            public void onAverageLoaded(double average) {
                                if (Double.isNaN(average) || Double.isInfinite(average)) {
                                    Log.e("PrincipalFragment", "El valor de la media no es válido: " + average);
                                    average = 0;
                                }
                                rivalMediaTextView.setText("Media: " + Math.round(average));
                                mediaRival = getMediaFromTextView(rivalMediaTextView);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e("PrincipalFragment", "Error obteniendo media rival: " + error);
                                rivalMediaTextView.setText("Media: 0");
                            }
                        });
                    } else {
                        rivalMediaTextView.setText("Media: 0");
                    }
                } else {
                    rivalMediaTextView.setText("Media: 0");
                    Log.e("PrincipalFragment", "pendientesJugar no es un Map");
                }
            } else {
                rivalMediaTextView.setText("Media: 0");
            }
        } else {
            rivalMediaTextView.setText("Media: 0");
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


    private void actualizarRivalPendiente(Map<String, Object> ligaData, TextView rivalTextView, View contenidoLigaLayout, TextView ligaFinalizadaTextView) {
        if (ligaData.containsKey("progresoLiga")) {
            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");

            if (progresoLiga.containsKey("pendientesJugar")) {
                try {
                    Object pendientesObj = progresoLiga.get("pendientesJugar");
                    if (pendientesObj instanceof Map) {
                        Map<String, Object> pendientesMapRaw = (Map<String, Object>) pendientesObj;
                        Map<String, Integer> pendientesMap = new HashMap<>();

                        for (Map.Entry<String, Object> entry : pendientesMapRaw.entrySet()) {
                            try {
                                pendientesMap.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                            } catch (Exception e) {
                                Log.e("PrincipalFragment", "Valor inválido para rival: " + entry.getKey());
                            }
                        }

                        if (!pendientesMap.isEmpty()) {
                            String proximoRival = Collections.max(pendientesMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                            rivalTextView.setText(proximoRival);

                            // Mostrar contenido normal
                            contenidoLigaLayout.setVisibility(View.VISIBLE);
                            ligaFinalizadaTextView.setVisibility(View.GONE);
                        } else {
                            // Ocultar todo y mostrar solo mensaje de liga terminada
                            contenidoLigaLayout.setVisibility(View.GONE);
                            ligaFinalizadaTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        rivalTextView.setText("Formato incorrecto de pendientes");
                        Log.e("PrincipalFragment", "pendientesJugar no es un Map");
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
        SharedPreferences prefs = requireContext().getSharedPreferences("MagosPrefs", Context.MODE_PRIVATE);

        // Obtener liga actual
        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        if (TextUtils.isEmpty(ligaName)) return;

        String claveArbitro = "arbitroPermisividad_" + ligaName;
        int arbitro = prefs.getInt(claveArbitro, 3);

        double probabilidadVictoria = calcularProbabilidadVictoria(mediaEquipo, mediaRival, arbitro);
        int porcentajeVictoria = (int) Math.round(probabilidadVictoria * 100);
        double resultado = new Random().nextDouble();

        String mensajeResultado = resultado < probabilidadVictoria
                ? "¡Has ganado el partido!\nPorcentaje de victoria: " + porcentajeVictoria + "%"
                : "Has perdido el partido.\nPorcentaje de victoria: " + porcentajeVictoria + "%";

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Resultado del Partido")
                .setMessage(mensajeResultado)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    PrincipalFragment nuevoFragment = new PrincipalFragment();
                    nuevoFragment.setArguments(getArguments());

                    transaction.replace(R.id.fragment_container, nuevoFragment);
                    transaction.commit();
                })
                .show();

        // Actualizar progreso liga directamente desde helper centralizado
        fireStoreHelper.actualizarProgresoLiga(ligaName, new FireStoreHelper.FirestoreUpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("PrincipalFragment", "Partido pendiente actualizado correctamente.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PrincipalFragment", "Error al actualizar partidos pendientes: " + errorMessage);
            }
        });

        // Generar nuevo árbitro
        int nuevoArbitro = new Random().nextInt(5) + 1;
        prefs.edit().putInt(claveArbitro, nuevoArbitro).apply();
        prefs.edit().putBoolean("mediaVisible_" + ligaName, false).apply();

    }



    private double calcularProbabilidadVictoria(double mediaEquipo, double mediaRival, int arbitroPermisividad) {
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

        // Ajuste por arbitraje
        double ajusteArbitraje = 0;
        if (arbitroPermisividad < 3) {
            ajusteArbitraje -= (intensidadAgresividad - 3) * 0.04;
            ajusteArbitraje += (intensidadContraataques - 3) * 0.02;
            ajusteArbitraje += (intensidadPresion - 3) * 0.03;
            ajusteArbitraje -= (intensidadPosesion - 3) * 0.02;
        } else if (arbitroPermisividad == 3) {
            ajusteArbitraje += (intensidadContraataques - 3) * 0.02;
            ajusteArbitraje += (intensidadPresion - 3) * 0.03;
        } else {
            ajusteArbitraje += (intensidadAgresividad - 3) * 0.04;
            ajusteArbitraje += (intensidadContraataques - 3) * 0.02;
            ajusteArbitraje += (intensidadPresion - 3) * 0.03;
            ajusteArbitraje += (intensidadPosesion - 3) * 0.02;
        }

        Log.d("PrincipalFragment", "Arbitro: " + arbitroPermisividad);
        Log.d("PrincipalFragment", "Ajuste por arbitraje: " + ajusteArbitraje);

        // Sumar los ajustes
        double probabilidadVictoria = probabilidadBase + ajusteMedia + ajusteInstalaciones + ajusteArbitraje;

        // Limitar la probabilidad entre 10% y 90%
        probabilidadVictoria = Math.max(0.1, Math.min(0.9, probabilidadVictoria));

        Log.d("PrincipalFragment", "Probabilidad de victoria calculada: " + (probabilidadVictoria * 100) + "%");

        return probabilidadVictoria;
    }


}
