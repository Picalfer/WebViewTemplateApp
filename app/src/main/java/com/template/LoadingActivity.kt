package com.template

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.TimeZone
import java.util.UUID

class LoadingActivity : AppCompatActivity() {

    private lateinit var url: String
    private lateinit var token: String
    private lateinit var analytics: FirebaseAnalytics

    private lateinit var singlePermissionPostNotifications: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        initFirebase()
        getUrl()
        initRegisterForActivityResult()
        getPermission()

    }

    private fun initFirebase() {
        analytics = Firebase.analytics

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            token = task.result
            Log.d("token", token)
        }
    }

    private fun getUrl() {
        val db = Firebase.firestore
        val docData = db.collection("database").document("check")

        docData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("result_db", "DocumentSnapshot data: ${document.data}")
                    url = document.data?.get("link").toString()
                    // webView.loadUrl(url)
                    Log.d("link", "DocumentSnapshot data: $url")

                    makeLink(url)
                    //Toast.makeText(this, url, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("result_db", "No such document")
                    openMainActivity()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("result_db", "get failed with ", exception)
                openMainActivity()
            }
    }

    private fun makeLink(url: String) {
        val userId = UUID.randomUUID().toString()
        val timeZone = TimeZone.getDefault()
        val link = "$url/?packageid=$packageName&usserid=$userId&getz=$timeZone&getr=utm_source=google-play&utm_medium=organic"
        Log.d("result_db", link)
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                singlePermissionPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                singlePermissionPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initRegisterForActivityResult() {
        singlePermissionPostNotifications =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when {
                        granted -> {
                            // уведомления разрешены
                        }

                        !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                            // пользователь нажал "больше не показывать"
                        }

                        else -> {
                            // уведомления запрещены, пользователь отклонил запрос
                        }
                    }
                }
            }
    }
}
