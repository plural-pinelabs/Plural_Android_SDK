package com.pinelabs.pluralsdk

import android.app.Activity
import android.content.Intent
import com.pinelabs.pluralsdk.activity.SplashActivity
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN

class PluralSDKManager {
    fun startPayment(activity: Activity, token: String) {
        PluralSDK.initializePluralSDK(activity)
        val intent = Intent(activity, SplashActivity::class.java)
        intent.putExtra(TOKEN, token)
        activity.startActivity(intent)
    }
}