package com.pinelabs.pluralsdk.callback

public interface PaymentResultCallBack {
     fun onErrorOccured(message: String?)
     fun onSuccessOccured()
     fun onTransactionResponse()
}