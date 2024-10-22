package com.pinelabs.pluralsdk

import android.app.Activity
import android.content.Context
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack

class PluralSDK {
    companion object {

        lateinit var pluralSDKObject: PluralSDKObject

        @Synchronized
        fun initializePluralSDK(activity: Activity){
            val callback: PaymentResultCallBack =   activity as PaymentResultCallBack
            pluralSDKObject = PluralSDKObject(activity, callback)
        }

        fun getInstance(): PluralSDKObject {
            return pluralSDKObject
        }

        fun destroyInstance() {
            pluralSDKObject = PluralSDKObject(null, null)
        }

        data class PluralSDKObject(val context: Context?, val callback: PaymentResultCallBack?)

    }
}