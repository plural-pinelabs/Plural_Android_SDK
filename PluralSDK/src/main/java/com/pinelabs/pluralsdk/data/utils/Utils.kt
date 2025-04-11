package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.clevertap.android.sdk.CleverTapAPI
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.utils.Constants.Companion.EMAIL_REGEX
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE_REGEX
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.TimeZone
import java.util.regex.Pattern
import com.pinelabs.pluralsdk.BuildConfig
import com.pinelabs.pluralsdk.data.model.SDKData
import com.pinelabs.pluralsdk.utils.Constants.Companion.APP_VERSION
import com.pinelabs.pluralsdk.utils.Constants.Companion.OS
import com.pinelabs.pluralsdk.utils.Constants.Companion.PLATFORM_TYPE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PLATFORM_VERSION
import com.pinelabs.pluralsdk.utils.Constants.Companion.SDK_TYPE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TRANSACTION_TYPE_SDK
import com.pinelabs.pluralsdk.utils.DeviceUtil

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

    fun isValidName(name: String): Boolean {
        val regex = "^[A-Za-z]+(?:[ '-][A-Za-z]+)*$".toRegex()
        return name.matches(regex)
    }

    fun isValidPincode(pincode: String): Boolean {
        val postalCodeRegex =
            "^([A-Za-z0-9]{3,10}[-\\s]?[A-Za-z0-9]{3,10})\$|^(\\d{4,10})\$|^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d\$|^\\d{5}(-\\d{4})?\$\n".toRegex()
        return pincode.matches(postalCodeRegex)
    }

    fun buttonBackground(context: Context, palette: Palette?): Drawable {

        val stateListDrawable = StateListDrawable()

        // Create different drawables for different states
        val pressedDrawable = GradientDrawable().apply {
            if (palette != null) {
                setColor(Color.parseColor(palette?.C900))
            } else {
                setColor(context.resources.getColor(R.color.header_color))
            }
            cornerRadius = 16f // Normal corner radius
        }

        // Add states to the StateListDrawable
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), pressedDrawable) // Default state

        return stateListDrawable
    }

    fun println(message: String) {
        if (BuildConfig.DEBUG) kotlin.io.println(message)

    }

    fun cleverTapLog() {
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.OFF)
    }

    fun createSDKData(context: Context): SDKData {
        return SDKData(
            TRANSACTION_TYPE_SDK,
            SDK_TYPE,
            APP_VERSION,
            APP_VERSION,
            BuildConfig.LIBRARY_PACKAGE_NAME,
            DeviceUtil.getDeviceName(),
            DeviceUtil.getDeviceId(context),
            PLATFORM_TYPE,
            OS,
            android.os.Build.VERSION.SDK,
            System.currentTimeMillis().toString(),
            PLATFORM_VERSION
        )
    }

}