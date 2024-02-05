package com.example.myapplication.networking

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

class NetworkState(val connectivityManager: ConnectivityManager) {

    // Check internet Connectivity
    fun performAction(action: () -> Unit) : Boolean {
        return if (hasValidInternetConnection()) {
            action()
            true
        } else {
            false
        }
    }

    private fun hasValidInternetConnection(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)?.state == NetworkInfo.State.CONNECTED
    }
}