package com.pinelabs.pluralsdk.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {
    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null

    fun init(smsBroadcastReceiverListener: SmsBroadcastReceiverListener?) {
        this.smsBroadcastReceiverListener = smsBroadcastReceiverListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action === SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras

            val smsRetreiverStatus = extras!![SmsRetriever.EXTRA_STATUS] as Status?

            when (smsRetreiverStatus!!.statusCode) {
                CommonStatusCodes
                    .SUCCESS -> {
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    println("SMS Receiver "+messageIntent.toString())
                    smsBroadcastReceiverListener?.onSuccess(messageIntent)
                }

                CommonStatusCodes.TIMEOUT -> smsBroadcastReceiverListener?.onFailure()
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)

        fun onFailure()
    }
}