package com.pinelabs.pluralsdk

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.pinelabs.pluralsdk.activity.SplashActivity
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN

class PluralSDKManager {
    fun startPayment(context: Context, token: String, callback: PaymentResultCallBack) {
        PluralSDK.initializePluralSDK(context, callback)
        val intent = Intent(context, SplashActivity::class.java)
        intent.putExtra(TOKEN, token)
        context.startActivity(intent)
    }
}