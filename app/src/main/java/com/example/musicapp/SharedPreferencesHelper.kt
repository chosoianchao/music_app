package com.example.musicapp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager


class SharedPreferencesHelper constructor(context: Context) {
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun putInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit { putBoolean(key, value) }
    }

    fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    fun putFloat(key: String, value: Float) {
        preferences.edit { putFloat(key, value) }
    }

    fun putLong(key: String, value: Long) {
        preferences.edit { putLong(key, value) }
    }

    fun getLong(key: String, default: Long): Long {
        return preferences.getLong(key, default)
    }

    fun getFloat(key: String, default: Float): Float {
        return preferences.getFloat(key, default)
    }

    fun getString(key: String, default: String): String {
        return preferences.getString(key, default).toString()
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return preferences.getBoolean(key, default)
    }

    fun getInt(key: String, default: Int): Int {
        return preferences.getInt(key, default)
    }

    fun clearPreferences() {
        preferences.edit().clear().apply()
    }
}