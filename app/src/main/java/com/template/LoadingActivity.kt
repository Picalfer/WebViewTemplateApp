package com.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoadingActivity : AppCompatActivity() {

    private lateinit var url: String
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        analytics = Firebase.analytics

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("token", token)
        }

        val db = Firebase.firestore
        val docData = db.collection("database").document("check")

        docData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("result_db", "DocumentSnapshot data: ${document.data}")
                    url = document.data?.get("link").toString()
                    // webView.loadUrl(url)
                    Log.d("link", "DocumentSnapshot data: $url")
                    //Toast.makeText(this, url, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("result_db", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("result_db", "get failed with ", exception)
            }
    }
}