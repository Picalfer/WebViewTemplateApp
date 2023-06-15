package com.template.storage

import android.content.Context
import android.content.SharedPreferences
import com.template.Constants

class AppPreferences(ctx: Context) {
    private val data: SharedPreferences = ctx
        .getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun setLink(link: String) {
        data.edit().putString(Constants.LINK, link).apply()
    }

    fun getLink(): String? {
        return data.getString(Constants.LINK, null)
    }

    fun setFirestoreState(state: String) {
        data.edit().putString(Constants.STATE, state).apply()
    }

    fun getFirestoreState(): String? {
        return data.getString(Constants.STATE, null)
    }
}