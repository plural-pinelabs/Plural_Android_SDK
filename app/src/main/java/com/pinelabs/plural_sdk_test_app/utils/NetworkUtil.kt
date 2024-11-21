package com.pinelabs.plural_sdk_test_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build

object NetworkUtil {
    fun isNetworkAvailable(context: Context): Boolean? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6.0 (API level 23) and above, use Network API
            val network: Network? = connectivityManager.activeNetwork
            network?.let {
                val networkInfo: NetworkInfo? = connectivityManager.getNetworkInfo(it)
                return networkInfo?.isConnected == true
            }
        } else {
            // For devices below Android 6.0 (API level 23), use getActiveNetworkInfo
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
        return false
    }
}