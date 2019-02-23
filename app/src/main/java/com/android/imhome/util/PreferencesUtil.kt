package com.android.imhome.util

import android.content.Context
import android.preference.PreferenceManager

class PreferencesUtil {
    companion object {

        private val LATITUDE = "LATITUDE"
        private val LONGITUDE = "LONGITUDE"
        private val RADIUS = "RADIUS"
        private val SSID = "SSID"
        private val ISHOMED = "ISHOMED"

        fun putLatitude(context: Context, lat: Double) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LATITUDE, lat.toString()).apply()
        }

        fun getLatitude(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE, "")
        }

        fun putLongitude(context: Context, longitude: Double) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LONGITUDE, longitude.toString()).apply()
        }

        fun getLongitude(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(LONGITUDE, "")
        }

        fun putRadius(context: Context, radius: Float) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(RADIUS, radius.toString()).apply()
        }

        fun getRadius(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(RADIUS, "")
        }

        fun putSSID(context: Context, ssid: String) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SSID, ssid).apply()
        }

        fun getSSID(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(SSID, "")
        }

        fun putIsHomed(context: Context, isHome: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ISHOMED, isHome).apply()
        }

        fun getIsHomed(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ISHOMED, false)
        }
    }
}