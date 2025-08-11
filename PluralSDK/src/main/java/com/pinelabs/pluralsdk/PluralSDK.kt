package com.pinelabs.pluralsdk

import android.content.Context
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack

class PluralSDK {
    companion object {

        lateinit var pluralSDKObject: PluralSDKObject
        private var orderID:String? = null

        fun initializePluralSDK(context: Context?, callback: PaymentResultCallBack) {
            pluralSDKObject = PluralSDKObject(context, callback)
        }

        fun getInstance(): PluralSDKObject {
            return pluralSDKObject
        }

        fun destroyInstance() {
            pluralSDKObject = PluralSDKObject(null, null)
        }

        fun setOrderID(orderId: String?) {
           orderID = orderId
        }
        
        fun getOrderID(): String? {
         return orderID
        }

        data class PluralSDKObject(val context: Context?, val callback: PaymentResultCallBack?)

    }
}
