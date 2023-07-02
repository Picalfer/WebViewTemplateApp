package com.template.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.template.Constants

class PushService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(Constants.TEST, "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(Constants.TEST, "From: ${message.from}")

        if (message.data.isNotEmpty()) {
            Log.d(Constants.TEST, "Message data payload: ${message.data}")
        }
    }
}