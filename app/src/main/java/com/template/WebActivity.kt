package com.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.template.webview.MyWebViewClient

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true)
        // указываем страницу загрузки
        // пока находится в LoadingActivity()
        val link = intent.getStringExtra("LINK") ?: "http//:404"
        webView.loadUrl(link)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}