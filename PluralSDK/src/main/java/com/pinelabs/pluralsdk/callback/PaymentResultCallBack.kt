package com.pinelabs.pluralsdk.callback

public interface PaymentResultCallBack {
    fun onErrorOccured(orderId: String?, code: String?, message: String?)
    fun onSuccessOccured(orderId: String?)
    fun onTransactionResponse()
    fun onCancelTransaction()
}