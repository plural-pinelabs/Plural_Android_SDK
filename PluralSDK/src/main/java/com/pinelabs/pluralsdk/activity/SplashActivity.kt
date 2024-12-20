package com.pinelabs.pluralsdk.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPLASH_TIMER
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import java.util.Timer
import java.util.TimerTask


class SplashActivity : AppCompatActivity() {

    private lateinit var token: String

    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        val clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        CleverTapUtil.CT_EVENT_SDK_INITIALISED(clevertapDefaultInstance, this)

        token = intent.getStringExtra(TOKEN).toString()
        token = "V3_jNUlI%2FKyVWDka2XLZgX0Y1xFb%2FFnFd7jXfv8U2fptHj5turL8D0ql%2BCw3lJJ3auk"

        bottomSheetDialog = BottomSheetDialog(this)

        //showMoreBankDialog()
        /*Show plural loader for given time period
        Pass token to subsequent activity*/

        Toast.makeText(this, "Version 1.0", Toast.LENGTH_SHORT).show()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val i = Intent(applicationContext, LandingActivity::class.java)
                i.putExtra(TOKEN, token)
                startActivity(i)
                finish()
            }
        }, SPLASH_TIMER)

    }

}