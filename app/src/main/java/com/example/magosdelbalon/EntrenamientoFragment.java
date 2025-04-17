package com.example.magosdelbalon;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.app.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class EntrenamientoFragment extends Fragment {

    private static final String TAG = "EntrenamientoFragment";

    private ListView listViewJugadores;
    private Button buttonEntrenar;
    private List<Jugador> listaJugadores;
    private ArrayAdapter<Jugador> adapter;

    private FireStoreHelper fireStoreHelper;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private boolean entrenamientoActivo = false;
    private Jugador jugadorEntrenandoActual;

    // Canal de notificación para Android 8 y versiones superiores
    private static final String CHANNEL_ID = "entrenamiento_channel";
    private static final String CHANNEL_NAME = "Entrenamiento";

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenamiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewJugadores = view.findViewById(R.id.listViewJugadores);
        buttonEntrenar = view.findViewById(R.id.buttonEntrenar);
        progressBar = view.findViewById(R.id.progressBarEntrenamiento);

        fireStoreHelper = new FireStoreHelper();

        listaJugadores = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaJugadores);
        listViewJugadores.setAdapter(adapter);

        cargarJugadores();

        buttonEntrenar.setOnClickListener(v -> mostrarDialogoSeleccionJugador());

        // Crear el canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Canal de notificación creado");
            } else {
                Log.e(TAG, "Error al obtener NotificationManager");
            }
        }

        // Restaurar el estado de la barra de progreso
        if (savedInstanceState != null) {
            int progress = savedInstanceState.getInt("progress", 0);
            boolean entrenamientoActivoRestaurado = savedInstanceState.getBoolean("entrenamientoActivo", false);

            progressBar.setProgress(progress);
            entrenamientoActivo = entrenamientoActivoRestaurado;

            if (entrenamientoActivo) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            // Si el entrenamiento está activo, reiniciar el temporizador
            if (entrenamientoActivo) {
                long duracionRestanteMs = jugadorEntrenandoActual != null ? jugadorEntrenandoActual.getTimestampFinEntrenamiento() - System.currentTimeMillis() : 0;
                if (duracionRestanteMs > 0) {
                    iniciarTemporizadorVisual(duracionRestanteMs);
                }
            }
        }
    }

    private void cargarJugadores() {
        String ligaName = getArguments().getString("leagueName");

        fireStoreHelper.obtenerJugadoresPorLiga(ligaName, new FireStoreHelper.JugadoresCallback() {
            @Override
            public void onJugadoresCargados(List<Jugador> jugadores) {
                listaJugadores.clear();
                long ahora = System.currentTimeMillis();

                for (Jugador jugador : jugadores) {
                    long fin = jugador.getTimestampFinEntrenamiento();

                    if (fin > 0 && ahora >= fin) {
                        jugador.finalizarEntrenamiento();
                        jugador.setTimestampFinEntrenamiento(0);

                        fireStoreHelper.actualizarJugador(ligaName, jugador, new FireStoreHelper.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Entrenamiento auto-finalizado para: " + jugador.getNombre());
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w(TAG, "Error al finalizar entrenamiento automáticamente", e);
                            }
                        });
                    } else if (fin > ahora && !entrenamientoActivo) {
                        entrenamientoActivo = true;
                        jugadorEntrenandoActual = jugador;
                        long duracionRestanteMs = fin - ahora;
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);
                        iniciarTemporizadorVisual(duracionRestanteMs);
                    }
                }

                listaJugadores.addAll(jugadores);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error al cargar jugadores", e);
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error al cargar jugadores", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarDialogoSeleccionJugador() {
        if (entrenamientoActivo) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Ya hay un jugador entrenando", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (listaJugadores.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(getContext(), "No tienes jugadores para entrenar.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String[] nombresJugadores = new String[listaJugadores.size()];
        for (int i = 0; i < listaJugadores.size(); i++) {
            nombresJugadores[i] = listaJugadores.get(i).getNombre();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un jugador")
                .setItems(nombresJugadores, (dialog, which) -> entrenarJugador(listaJugadores.get(which)))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void entrenarJugador(Jugador jugador) {
        if (entrenamientoActivo) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Ya hay un jugador en entrenamiento", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String ligaName = getArguments().getString("leagueName");
        if (ligaName == null || ligaName.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Nombre de la liga no encontrado", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        jugador.iniciarEntrenamiento();

        long ahora = System.currentTimeMillis();
        long finEntrenamiento = ahora + 6000; // 6 segundos
        jugador.setTimestampFinEntrenamiento(finEntrenamiento);

        entrenamientoActivo = true;
        jugadorEntrenandoActual = jugador;

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        fireStoreHelper.actualizarJugador(ligaName, jugador, new FireStoreHelper.SimpleCallback() {
            @Override
            public void onSuccess() {
                iniciarTemporizadorVisual(6000);
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error al iniciar entrenamiento", Toast.LENGTH_SHORT).show();
                }
                entrenamientoActivo = false;
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void iniciarTemporizadorVisual(long duracionRestanteMs) {
        int totalSegundos = (int) (duracionRestanteMs / 1000);
        progressBar.setMax(totalSegundos);
        progressBar.setProgress(0);

        countDownTimer = new CountDownTimer(duracionRestanteMs, 1000) {
            int progreso = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                progreso++;
                progressBar.setProgress(progreso);
            }

            @Override
            public void onFinish() {
                if (jugadorEntrenandoActual != null) {
                    jugadorEntrenandoActual.finalizarEntrenamiento();
                    jugadorEntrenandoActual.setTimestampFinEntrenamiento(0);

                    String ligaName = getArguments().getString("leagueName");
                    fireStoreHelper.actualizarJugador(ligaName, jugadorEntrenandoActual, new FireStoreHelper.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            if (isAdded()) {
                                // Enviar notificación
                                enviarNotificacion("Entrenamiento completado", "El entrenamiento de " + jugadorEntrenandoActual.getNombre() + " ha terminado.");
                                Toast.makeText(getContext(), "Entrenamiento completado: " + jugadorEntrenandoActual.getNombre(), Toast.LENGTH_SHORT).show();
                            }
                            cargarJugadores();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Error al actualizar después del entrenamiento", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                entrenamientoActivo = false;
                progressBar.setVisibility(View.GONE);
            }
        }.start();
    }

    private void enviarNotificacion(String title, String content) {
        Notification.Builder builder = new Notification.Builder(getContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notificacion) // Asegúrate de tener este icono en drawable
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }
}
