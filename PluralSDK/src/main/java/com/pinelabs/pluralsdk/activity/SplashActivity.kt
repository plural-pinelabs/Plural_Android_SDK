package com.pinelabs.pluralsdk.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.clevertap.android.sdk.CleverTapAPI
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPLASH_TIMER
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import java.util.Timer
import java.util.TimerTask

class SplashActivity : AppCompatActivity() {

    private lateinit var token: String

    private lateinit var logoAnimation: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        val clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this@SplashActivity)
        CleverTapUtil.CT_EVENT_SDK_INITIALISED(clevertapDefaultInstance, this@SplashActivity)

        token = intent.getStringExtra(TOKEN).toString()

        logoAnimation = findViewById(R.id.img_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

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