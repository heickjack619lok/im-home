package com.android.imhome.util

import android.content.Context
import android.net.wifi.WifiManager

class Util {
    companion object {
        fun isConnectedHomeWiFi(context: Context): Boolean {
            val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            val ssid = info.ssid.replace("\"", "")

            return ssid.equals(PreferencesUtil.getSSID(context))
        }

        fun isHomed(context: Context): Boolean{
            return isConnectedHomeWiFi(context) || PreferencesUtil.getIsHomed(context)
        }
    }
}