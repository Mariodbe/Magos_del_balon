package com.example.magosdelbalon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "canal_mensajes";
    private static final String CHANNEL_NAME = "Canal de Mensajes";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCMService", "Mensaje recibido: " + remoteMessage.toString());

        String titulo = "";
        String cuerpo = "";

        if (remoteMessage.getNotification() != null) {
            titulo = remoteMessage.getNotification().getTitle();
            cuerpo = remoteMessage.getNotification().getBody();
        }

        Log.d("FCMService", "Notificación recibida - Título: " + titulo + ", Cuerpo: " + cuerpo);

        mostrarNotificacion(titulo, cuerpo);
    }


    private void mostrarNotificacion(String titulo, String cuerpo) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones de mensajes");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent para abrir la app al tocar la notificación
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(R.drawable.ic_chat) // Asegúrate de tener un icono válido aquí
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Mostrar la notificación
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
            Log.d("FCMService", "Notificación mostrada correctamente");
        }
    }


}
