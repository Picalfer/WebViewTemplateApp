package com.template

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.template.webview.MyWebViewClient

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var webViewBundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webView = findViewById(R.id.webView)

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            val link = intent.getStringExtra(Constants.LINK)
            initWebView(link!!)
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        webViewBundle = Bundle()
        webView.saveState(webViewBundle!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(link: String) {
        webView.webViewClient = MyWebViewClient()
        webView.settings.apply {
            allowFileAccess = true
            javaScriptEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }
        webView.loadUrl(link)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}