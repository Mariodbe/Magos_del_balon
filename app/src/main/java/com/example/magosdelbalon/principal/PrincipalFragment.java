package com.example.magosdelbalon.principal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.magosdelbalon.FireStoreHelper;
import com.example.magosdelbalon.MainActivity;
import com.example.magosdelbalon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private String equipoActual;
    private String equipoRival;



    public PrincipalFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PrincipalFragment", "Inflando layout del fragment");

        View rootView = inflater.inflate(R.layout.fragment_principal, container, false);

        // Obtener argumentos
        Bundle args = getArguments();
        String ligaName = args != null ? args.getString("leagueName") : null;
        Log.d("PrincipalFragment", "Liga recibida en fragment: " + ligaName);

        SharedPreferences prefs = requireContext().getSharedPreferences("MagosPrefs", Context.MODE_PRIVATE);
        String claveArbitro = "arbitroPermisividad_" + ligaName;
        int permisividadArbitro = args != null ? args.getInt("arbitroPermisividad", -1) : -1;

        // Si no hay valor guardado, generamos uno y lo guardamos
        if (permisividadArbitro == -1) {
            permisividadArbitro = prefs.getInt(claveArbitro, new Random().nextInt(5) + 1);
            prefs.edit().putInt(claveArbitro, permisividadArbitro).apply();
        }
        ImageView homeTeamShield = rootView.findViewById(R.id.image_view_home_team_shield);
        TextView textViewPermisividad = rootView.findViewById(R.id.text_view_arbitro_permisividad);
        textViewPermisividad.setText("Arbitro: "+ nivelPermisividad(permisividadArbitro)+" ");

        ProgressBar progressBar = rootView.findViewById(R.id.progress_bar_permisividad);
        // Invertir el progreso: 1 -> lleno, 5 -> vacío
        int progresoInvertido = progressBar.getMax() + 1 - permisividadArbitro;
        progressBar.setProgress(progresoInvertido);
        Log.d("", "Permisividad real: " + permisividadArbitro + ", Progreso invertido: " + progresoInvertido);

        Drawable drawable = progressBar.getProgressDrawable().mutate();
        int color = getColorForPermisividad(permisividadArbitro);
        drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

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

    private String nivelPermisividad(int valor) {
        switch (valor) {
            case 1: return "Muy Estricto";
            case 2: return "Estricto";
            case 3: return "Moderado";
            case 4: return "Flexible";
            case 5: return "Muy Permisivo";
            default: return "Desconocido";
        }
    }


    private int getColorForPermisividad(int permisividad) {
        switch (permisividad) {
            case 1:
                return Color.parseColor("#FF0000"); // Rojo
            case 2:
                return Color.parseColor("#FF8000"); // Naranja
            case 3:
                return Color.parseColor("#FFFF00"); // Amarillo
            case 4:
                return Color.parseColor("#80FF00"); // Verde Lima
            case 5:
                return Color.parseColor("#008f39"); //Verde Puro
            default:
                return Color.parseColor("#0000FF"); // Azul
        }
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
                String proximorival = actualizarRivalPendiente(ligaData, rivalTextView, getView().findViewById(R.id.layout_contenido_liga),
                        getView().findViewById(R.id.text_view_liga_finalizada), getView().findViewById(R.id.text_view_ganador));
                // Calcular la media del equipo rival
                calcularMediaRival(proximorival, rivalMediaTextView);
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
            equipoActual = equipo;  // Guardar el nombre del equipo

            // Cargar imagen del equipo
            String nombreEquipoDrawable = MainActivity.limpiarNombreParaDrawable(equipo);
            Log.d("PrincipalFragment", "Intentando cargar el drawable: " + nombreEquipoDrawable); // Log para depuración
            int resIdEquipo = getResources().getIdentifier(nombreEquipoDrawable, "drawable", getActivity().getPackageName());
            if (resIdEquipo != 0) {
                ImageView homeTeamShield = getView().findViewById(R.id.image_view_home_team_shield);
                homeTeamShield.setImageResource(resIdEquipo);
            } else {
                Log.e("PrincipalFragment", "No se encontró el drawable para el equipo: " + equipo);
                // Establecer un drawable por defecto si no se encuentra el drawable del equipo
                ImageView homeTeamShield = getView().findViewById(R.id.image_view_home_team_shield);
            }
        } else {
            teamTextView.setText("Desconocido");
            equipoActual = "Desconocido";
        }
    }


    private void calcularMediaRival(String nombreRival, TextView rivalMediaTextView) {
        if (nombreRival == null || nombreRival.isEmpty()) {
            rivalMediaTextView.setText("Media: 0");
            return;
        }

        // Establecer el nombre del equipo rival en el TextView correspondiente
        TextView rivalTextView = getView().findViewById(R.id.text_view_away_team_name);
        rivalTextView.setText(nombreRival);

        // Cargar el escudo del equipo rival
        String nombreRivalDrawable = MainActivity.limpiarNombreParaDrawable(nombreRival);
        int resIdRival = getResources().getIdentifier(nombreRivalDrawable, "drawable", getActivity().getPackageName());
        ImageView awayTeamShield = getView().findViewById(R.id.image_view_away_team_shield);

        if (resIdRival != 0) {
            awayTeamShield.setImageResource(resIdRival);
        } else {
            Log.e("PrincipalFragment", "No se encontró el drawable para el rival: " + nombreRival);
        }

        // Calcular y mostrar la media del equipo rival
        fireStoreHelper.fetchRivalAverageByTeamName(nombreRival, new FireStoreHelper.AverageCallback() {
            @Override
            public void onAverageLoaded(double average) {
                if (Double.isNaN(average) || Double.isInfinite(average)) {
                    Log.e("PrincipalFragment", "El valor de la media no es válido: " + average);
                    average = 0;
                }
                rivalMediaTextView.setText("Media: " + Math.round(average));
                mediaRival = average;
            }

            @Override
            public void onError(String error) {
                Log.e("PrincipalFragment", "Error obteniendo media rival: " + error);
                rivalMediaTextView.setText("Media: 0");
            }
        });
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


    private String actualizarRivalPendiente(Map<String, Object> ligaData, TextView rivalTextView, View contenidoLigaLayout, TextView ligaFinalizadaTextView, TextView textViewGanador) {
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
                            equipoRival = proximoRival; // variable de clase

                            contenidoLigaLayout.setVisibility(View.VISIBLE);
                            ligaFinalizadaTextView.setVisibility(View.GONE);

                            return proximoRival; // devolver el rival encontrado
                        } else {
                            // No hay más partidos -> Buscar ganador
                            contenidoLigaLayout.setVisibility(View.GONE);
                            ligaFinalizadaTextView.setVisibility(View.VISIBLE);

// Obtener clasificación
                            List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
                            if (clasificacion != null && !clasificacion.isEmpty()) {
                                // 1. Calcular puntos y diferencia de goles del equipo propio
                                int misGanados = toInt(progresoLiga.get("MIpartidosGanados"));
                                int misEmpatados = toInt(progresoLiga.get("MIpartidosEmpatados"));
                                int misGolesAFavor = toInt(progresoLiga.get("MIgolesAFavor"));
                                int misGolesEnContra = toInt(progresoLiga.get("MIgolesEnContra"));
                                int misPuntos = misGanados * 3 + misEmpatados;
                                int miDiferenciaGoles = misGolesAFavor - misGolesEnContra;

                                // 2. Buscar el equipo con más puntos y mayor diferencia de goles de la clasificación
                                Map<String, Object> mejorEquipo = null;
                                int maxPuntos = -1;
                                int maxDiferenciaGoles = -1;

                                for (Map<String, Object> equipo : clasificacion) {
                                    int ganados = toInt(equipo.get("partidosGanados"));
                                    int empatados = toInt(equipo.get("partidosEmpatados"));
                                    int golesAFavor = toInt(equipo.get("golesAFavor"));
                                    int golesEnContra = toInt(equipo.get("golesEnContra"));
                                    int puntos = ganados * 3 + empatados;
                                    int diferenciaGoles = golesAFavor - golesEnContra;

                                    if (puntos > maxPuntos || (puntos == maxPuntos && diferenciaGoles > maxDiferenciaGoles)) {
                                        maxPuntos = puntos;
                                        maxDiferenciaGoles = diferenciaGoles;
                                        mejorEquipo = equipo;
                                    }
                                }

                                // 3. Comparar con mi equipo
                                if (mejorEquipo != null) {
                                    int puntosGanador = maxPuntos;
                                    int diferenciaGolesGanador = maxDiferenciaGoles;

                                    if (puntosGanador > misPuntos) {
                                        String nombreGanador = (String) mejorEquipo.get("equipo");
                                        textViewGanador.setText("El ganador de la liga es: " + nombreGanador);
                                    } else if (puntosGanador == misPuntos) {
                                        if (diferenciaGolesGanador > miDiferenciaGoles) {
                                            String nombreGanador = (String) mejorEquipo.get("equipo");
                                            textViewGanador.setText("El ganador de la liga es: " + nombreGanador);
                                        } else {
                                            textViewGanador.setText("¡Has ganado la liga!");
                                        }
                                    } else {
                                        textViewGanador.setText("¡Has ganado la liga!");
                                    }
                                } else {
                                    textViewGanador.setText("No hay datos de clasificación.");
                                }

                                textViewGanador.setVisibility(View.VISIBLE);
                            } else {
                                textViewGanador.setText("No hay datos de clasificación.");
                                textViewGanador.setVisibility(View.VISIBLE);
                            }
                            return null;
                        }
                    } else {
                        rivalTextView.setText("Formato incorrecto de pendientes");
                        Log.e("PrincipalFragment", "pendientesJugar no es un Map");
                        return null;
                    }
                } catch (Exception e) {
                    Log.e("PrincipalFragment", "Error al obtener rival: " + e.getMessage());
                    rivalTextView.setText("Error al obtener rival");
                    return null;
                }
            } else {
                rivalTextView.setText("Sin rivales pendientes");
                return null;
            }
        } else {
            rivalTextView.setText("Progreso no disponible");
            return null;
        }
    }

    private int toInt(Object value) {
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        return 0;
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
        String ligaName = getArguments() != null ? getArguments().getString("leagueName") : null;
        if (TextUtils.isEmpty(ligaName)) return;

        String claveArbitro = "arbitroPermisividad_" + ligaName;
        int arbitro = prefs.getInt(claveArbitro, 3);

        double probVictoriaBase = calcularProbabilidadVictoria(mediaEquipo, mediaRival, arbitro);
        double diferenciaMedias = Math.abs(mediaEquipo - mediaRival);
        double probEmpate = 0.3 - Math.min(0.25, diferenciaMedias * 0.025);
        probEmpate += new Random().nextDouble() * 0.05 - 0.025;
        probEmpate = Math.max(0.05, Math.min(0.3, probEmpate));

        double factor = 1.0 - probEmpate;
        double probVictoria = probVictoriaBase * factor;
        int porcentajeVictoria = (int) Math.round(probVictoria * 100);
        double resultado = new Random().nextDouble();

        String mensajeResultado;
        String resultadoPartido;
        long resultadoDinero = 0;
        // Simular goles de forma coherente
        int golesEquipo, golesRival;
        Random random = new Random();

        if (resultado < probVictoria) {
            // Victoria
            golesEquipo = 1 + random.nextInt(4); // entre 1 y 4
            golesRival = random.nextInt(golesEquipo); // siempre menor
            mensajeResultado = "¡Has ganado el partido!\n" + equipoActual + " " + golesEquipo + " - " + golesRival + " " + equipoRival;
            resultadoDinero = 1_500_000;
            resultadoPartido = "ganado";
        } else if (resultado < probVictoria + probEmpate) {
            // Empate
            golesEquipo = random.nextInt(4); // entre 0 y 3
            golesRival = golesEquipo;
            mensajeResultado = "El partido terminó en empate.\n" + equipoActual + " " + golesEquipo + " - " + golesRival + " " + equipoRival;
            resultadoPartido = "empatado";
            resultadoDinero = 1_000_000;
        } else {
            // Derrota
            golesRival = 1 + random.nextInt(4); // entre 1 y 4
            golesEquipo = random.nextInt(golesRival); // siempre menor
            mensajeResultado = "Has perdido el partido.\n" + equipoActual + " " + golesEquipo + " - " + golesRival + " " + equipoRival;
            resultadoPartido = "perdido";
            resultadoDinero = 500_000;
        }


        Log.d("Simulacion", "Resultado: " + resultadoPartido + ", Dinero ganado: " + resultadoDinero);

        fireStoreHelper.actualizarDineroPorResultado(ligaName, resultadoDinero, new FireStoreHelper.FirestoreUpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("FIREBASE", "Dinero actualizado correctamente.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FIREBASE", "Error al actualizar dinero: " + errorMessage);
                Toast.makeText(requireContext(), "Error al actualizar el dinero", Toast.LENGTH_SHORT).show();
            }
        });
        fireStoreHelper.actualizarGolesAFavorYEnContra(ligaName, equipoRival, golesEquipo, golesRival);
        fireStoreHelper.actualizarEstadisticasPartido(ligaName, equipoActual, equipoRival, resultadoPartido, new FireStoreHelper.FirestoreUpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("Estadísticas", "Estadísticas de partido actualizadas correctamente.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Estadísticas", "Error al actualizar estadísticas: " + errorMessage);
            }
        });

        fireStoreHelper.obtenerDatosLigaPorId(ligaName, new FireStoreHelper.FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                if (ligaData != null && ligaData.containsKey("progresoLiga")) {
                    Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
                    Map<String, Integer> pendientes = (Map<String, Integer>) progresoLiga.get("pendientesJugar");
                    if (pendientes != null && !pendientes.isEmpty()) {
                        String proximoRival = Collections.max(pendientes.entrySet(), Map.Entry.comparingByValue()).getKey();
                        List<List<Map<String, Object>>> grupos = obtenerGruposDeClasificacionSinRival(ligaData, proximoRival);

                        for (int i = 0; i < grupos.size(); i++) {
                            List<Map<String, Object>> grupo = grupos.get(i);
                            StringBuilder sb = new StringBuilder("Grupo " + (i + 1) + ": ");
                            for (Map<String, Object> equipo : grupo) {
                                sb.append(equipo.get("equipo")).append(" ");
                            }
                            Log.d("GRUPOS_CLASIFICACION", sb.toString().trim());
                            simularGrupoDeDos(grupo, ligaName);
                        }
                    } else {
                        Log.d("GRUPOS_CLASIFICACION", "No hay rivales pendientes");
                    }
                } else {
                    Log.d("GRUPOS_CLASIFICACION", "Datos de liga o progresoLiga no disponibles");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("GRUPOS_CLASIFICACION", "Error al obtener ligaData: " + errorMessage);
            }
        });

        // Generar nuevo valor de permisividad del árbitro
        int nuevoArbitro = new Random().nextInt(5) + 1;
        prefs.edit().putInt(claveArbitro, nuevoArbitro).apply();

        new AlertDialog.Builder(requireContext())
                .setTitle("Resultado del Partido")
                .setMessage(mensajeResultado)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    Bundle args = new Bundle();
                    args.putString("leagueName", ligaName);
                    args.putInt("arbitroPermisividad", nuevoArbitro);

                    PrincipalMainFragment nuevoFragment = new PrincipalMainFragment();
                    nuevoFragment.setArguments(args);

                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, nuevoFragment);
                    transaction.commit();
                })
                .show();

            // Actualizar visualmente la permisividad en el hilo de la interfaz de usuario
            requireActivity().runOnUiThread(() -> {
                ProgressBar progressBar = getView().findViewById(R.id.progress_bar_permisividad);

                // Invertir el progreso: 1 -> lleno, 5 -> vacío
                progressBar.setProgress(progressBar.getMax() - nuevoArbitro + 1);

                Drawable drawable = progressBar.getProgressDrawable().mutate();
                int color = getColorForPermisividad(nuevoArbitro);
                drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

            });

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

    private List<List<Map<String, Object>>> obtenerGruposDeClasificacionSinRival(Map<String, Object> ligaData, String proximoRival) {
        List<Map<String, Object>> clasificacion = (List<Map<String, Object>>)
                ((Map<String, Object>) ligaData.get("progresoLiga")).get("clasificacion");

        List<Map<String, Object>> clasificacionFiltrada = new ArrayList<>();
        for (Map<String, Object> equipo : clasificacion) {
            String nombreEquipo = (String) equipo.get("equipo");
            if (!nombreEquipo.equalsIgnoreCase(proximoRival)) {
                clasificacionFiltrada.add(equipo);
            }
        }

        List<List<Map<String, Object>>> gruposDeDos = new ArrayList<>();
        for (int i = 0; i < clasificacionFiltrada.size(); i += 2) {
            List<Map<String, Object>> grupo = new ArrayList<>();
            grupo.add(clasificacionFiltrada.get(i));
            if (i + 1 < clasificacionFiltrada.size()) {
                grupo.add(clasificacionFiltrada.get(i + 1));
            }
            gruposDeDos.add(grupo);
        }

        return gruposDeDos;
    }


    private void simularGrupoDeDos(List<Map<String, Object>> grupo, String ligaName) {
        if (grupo.size() != 2) {
            Log.e("SIMULACION_GRUPO", "El grupo no tiene exactamente dos equipos.");
            return;
        }

        String equipoA = (String) grupo.get(0).get("equipo");
        String equipoB = (String) grupo.get(1).get("equipo");

        fireStoreHelper.fetchRivalAverageByTeamName(equipoA, new FireStoreHelper.AverageCallback() {
            @Override
            public void onAverageLoaded(double mediaA) {
                fireStoreHelper.fetchRivalAverageByTeamName(equipoB, new FireStoreHelper.AverageCallback() {
                    @Override
                    public void onAverageLoaded(double mediaB) {
                        int arbitro = new Random().nextInt(5) + 1; // árbitro aleatorio para la simulación

                        double probVictoriaA = calcularProbabilidadVictoria(mediaA, mediaB, arbitro);

                        double diferenciaMedias = Math.abs(mediaA - mediaB);
                        double probEmpate = 0.3 - Math.min(0.25, diferenciaMedias * 0.025);
                        probEmpate += new Random().nextDouble() * 0.05 - 0.025;
                        probEmpate = Math.max(0.05, Math.min(0.3, probEmpate));

                        double factor = 1.0 - probEmpate;
                        probVictoriaA *= factor;
                        double probVictoriaB = 1.0 - probEmpate - probVictoriaA;

                        double resultado = new Random().nextDouble();
                        String resultadoFinal;
                        String resultadoTexto;

                        int golesA, golesB;
                        Random random = new Random();

                        if (resultado < probVictoriaA) {
                            // Gana equipo A
                            golesA = 1 + random.nextInt(4); // 1-4
                            golesB = random.nextInt(golesA); // menos que A
                            resultadoTexto = equipoA + " " + golesA + " - " + golesB + " " + equipoB + " (Ganador: " + equipoA + ")";
                            resultadoFinal = "A";
                        } else if (resultado < probVictoriaA + probEmpate) {
                            // Empate
                            golesA = random.nextInt(4); // 0-3
                            golesB = golesA;
                            resultadoTexto = equipoA + " " + golesA + " - " + golesB + " " + equipoB + " (Empate)";
                            resultadoFinal = "E";
                        } else {
                            // Gana equipo B
                            golesB = 1 + random.nextInt(4); // 1-4
                            golesA = random.nextInt(golesB); // menos que B
                            resultadoTexto = equipoA + " " + golesA + " - " + golesB + " " + equipoB + " (Ganador: " + equipoB + ")";
                            resultadoFinal = "B";
                        }

                        Log.d("SIMULACION_GRUPO", resultadoTexto);

                        // Actualizar Firestore con el resultado
                        fireStoreHelper.actualizarGolesEnClasificacion(ligaName, equipoA, equipoB, golesA, golesB, () -> {
                            fireStoreHelper.actualizarClasificacionEnFirestore(ligaName, equipoA, equipoB, resultadoFinal);
                        });

                    }

                    @Override
                    public void onError(String error) {
                        Log.e("SIMULACION_GRUPO", "Error al obtener media de " + equipoB + ": " + error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("SIMULACION_GRUPO", "Error al obtener media de " + equipoA + ": " + error);
            }
        });
    }

}
