package com.xose.quizzbattle.model

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Manejar el mensaje aquí (datos o notificación)
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // Aquí puedes mostrar una notificación usando NotificationManager
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Aquí puedes enviar el token al servidor si necesitas identificar al dispositivo
        Log.d("FCM", "Nuevo token: $token")
    }
}