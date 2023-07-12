package com.template

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.template.notification.PushService
import com.template.webview.MyWebViewClient

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var webViewBundle: Bundle? = null
    private lateinit var pushBroadcastReceiver: BroadcastReceiver

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

        pushBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras
                Log.d("TEST", "Message received")
                extras?.keySet()?.let { key ->
                    Log.d("TEST", "Action key -> $key")

                    Log.d("TEST", "Message key -> ${extras.getString(key.toString())}")
                    Toast.makeText(
                        applicationContext,
                        extras.getString(key.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(PushService.INTENT_FILTER)

        registerReceiver(pushBroadcastReceiver, intentFilter)
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
            Log.d(Constants.TEST, "User agent: $userAgentString")
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pushBroadcastReceiver)
    }
}