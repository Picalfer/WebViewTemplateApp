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

        val link = intent.getStringExtra(Constants.LINK)
        initWebView(link!!)
    }

    private fun initWebView(link: String) {
        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl(link)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}