package com.template.notification

import com.google.firebase.messaging.FirebaseMessagingService

class PushService: FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }
}