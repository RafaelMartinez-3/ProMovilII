package com.example.respuestaautomatica.receiver

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.respuestaautomatica.R

class CallService : Service() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            startForeground(
                1,
                createNotification(),
                FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(1, createNotification())
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("CallService", "Ejecutando servicio...")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "call_service_channel"
        val channelName = "Call Service"

        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio de Respuesta Automática")
            .setContentText("Escuchando llamadas entrantes...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CallService", "Servicio detenido, reiniciando...")
        val broadcastIntent = Intent(this, CallReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }

}
