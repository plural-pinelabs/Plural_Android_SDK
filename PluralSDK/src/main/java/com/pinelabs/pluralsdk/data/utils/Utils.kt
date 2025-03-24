package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import android.graphics.PixelFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pinelabs.pluralsdk.utils.Constants.Companion.EMAIL_REGEX
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE_REGEX
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.TimeZone
import java.util.regex.Pattern


object Utils {

    fun hasInternetConnection(context: Context?): Boolean {
        try {
            if (context == null)
                return false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    fun getTimeOffset(): Int {
        // Get the default time zone of the device
        val timeZone: TimeZone = TimeZone.getDefault()
        // Get the offset from UTC in milliseconds
        val offsetMillis: Int = timeZone.getOffset(System.currentTimeMillis())
        // Convert milliseconds to minutes
        val offsetMinutes = offsetMillis / (1000 * 60)
        return offsetMinutes
    }

    // Method to map the pixel format to color depth in bits
    fun getColorDepth(pixelFormat: Int): Int {
        return when (pixelFormat) {
            PixelFormat.RGBA_8888 -> 32 // 32 bits per pixel
            PixelFormat.RGB_565 -> 16 // 16 bits per pixel
            PixelFormat.RGBA_4444 -> 16 // 16 bits per pixel
            PixelFormat.RGBX_8888 -> 32 // 32 bits per pixel
            else -> 24 // Default to 24 bits per pixel
        }
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        val regex = MOBILE_REGEX
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }

    fun isValidEmail(email: String?): Boolean {
        val regex = EMAIL_REGEX
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}