package com.pinelabs.pluralsdk

import android.content.Context
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack

class PluralSDK {
    companion object {

        lateinit var pluralSDKObject: PluralSDKObject

        //@Synchronized
        fun initializePluralSDK(context: Context?, callback: PaymentResultCallBack) {
            //val callback: PaymentResultCallBack =   activity as PaymentResultCallBack
            pluralSDKObject = PluralSDKObject(context, callback)
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