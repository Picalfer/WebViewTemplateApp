package com.template

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this@App)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(Constants.TEST, "token: $token")
        }

    }
}