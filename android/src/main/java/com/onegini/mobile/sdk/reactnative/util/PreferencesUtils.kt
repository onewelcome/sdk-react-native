package com.onegini.mobile.sdk.reactnative.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtils {
    private const val PREF_NAME = "pinProfileConfig"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, MODE)
    }

    @JvmStatic fun setPinLength(profileId: String, pinLength: Int) {
        val editor = preferences.edit()
        editor.putInt(profileId, pinLength)
        editor.apply()
    }

    @JvmStatic fun getPinLength(profileId: String): Int {
        return preferences.getInt(profileId, 5)
    }

}