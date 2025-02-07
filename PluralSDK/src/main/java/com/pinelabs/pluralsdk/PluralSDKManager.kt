package com.pinelabs.pluralsdk

import android.content.Context
import android.content.Intent
import com.pinelabs.pluralsdk.activity.SplashActivity
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.utils.Constants.Companion.API_INTERNET_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN

class PluralSDKManager {
    fun startPayment(context: Context, token: String, callback: PaymentResultCallBack) {

        PluralSDK.initializePluralSDK(context, callback)
        if (Utils.hasInternetConnection(context)) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.putExtra(TOKEN, token)
            context.startActivity(intent)
        } else {
            PluralSDK.getInstance().callback!!.onErrorOccured(null,"-1", API_INTERNET_MESSAGE)
        }

    }
}