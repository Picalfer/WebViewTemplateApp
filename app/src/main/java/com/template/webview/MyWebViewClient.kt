package com.template.webview

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.template.Constants

class MyWebViewClient : WebViewClient() {
    override fun onPageFinished(view: WebView, url: String) {
        CookieManager.getInstance().flush()
        Log.d(Constants.TEST, "title webView: ${view.title}")
    }
}
