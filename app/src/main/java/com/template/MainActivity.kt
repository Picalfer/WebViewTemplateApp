package com.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var url: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore

        val docData = db.collection("database").document("check")

        docData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("result_db", "DocumentSnapshot data: ${document.data}")
                    url = document.data?.get("link").toString()
                    webView.loadUrl(url)
                    Log.d("link", "DocumentSnapshot data: ${url}")
                } else {
                    Log.d("result_db", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("result_db", "get failed with ", exception)
            }

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true)
        // указываем страницу загрузки

    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}