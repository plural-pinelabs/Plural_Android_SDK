package com.pinelabs.pluralsdk.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R

class SuccessActivity : AppCompatActivity() {

    private val AUTO_CLOSE_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_success)

        PluralSDK.getInstance().callback!!.onSuccessOccured()

        Handler().postDelayed({
            finish() // Close
        }, AUTO_CLOSE_DELAY)

    }
}
