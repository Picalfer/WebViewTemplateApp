package com.template.network

import android.content.Context
import android.net.ConnectivityManager


object NetworkManager {
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return (networkInfo != null) && networkInfo.isConnected
    }
}