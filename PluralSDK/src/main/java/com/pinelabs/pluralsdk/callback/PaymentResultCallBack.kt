package com.pinelabs.pluralsdk.callback

 interface PaymentResultCallBack {
    fun onErrorOccured(orderId: String?, code: String?, message: String?)
    fun onSuccessOccured(orderId: String?)
    fun onCancelTransaction()
}