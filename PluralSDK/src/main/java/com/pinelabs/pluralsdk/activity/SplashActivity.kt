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

        //CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);    //Default Log level
        val clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this@SplashActivity)
        CleverTapUtil.CT_EVENT_SDK_INITIALISED(clevertapDefaultInstance, this@SplashActivity)

        token = intent.getStringExtra(TOKEN).toString()
        token ="V3_QUmBg93rH58Emfwaf37dx%2F4xSxyBVRr3qYQI%2Fatmdk6r%2BO1Nl%2B9Na249%2FF%2F08nbk"

        bottomSheetDialog = BottomSheetDialog(this)

        //showMoreBankDialog()
        /*Show plural loader for given time period
        Pass token to subsequent activity*/

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