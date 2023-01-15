package com.example.ar_natk.data.storage

import android.content.Context
import com.example.ar_natk.utils.Constants

class FirstRunStorage(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.RUN_PREFS_KEY, Context.MODE_PRIVATE)

    var isFirstRun: Boolean
        get() = prefs.getBoolean(Constants.RUN_KEY, true)
        set(value) {
            prefs.edit().putBoolean(Constants.RUN_KEY, value).apply()
        }

    fun clearData() {
        prefs.edit().clear().apply()
    }
}