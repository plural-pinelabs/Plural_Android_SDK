package com.pinelabs.pluralsdk.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
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

        token = intent.getStringExtra(TOKEN).toString()
        //token = "S01dN7NIKpzd42AJPti1dF5BT6EKd1%2FPB2o9B82xppyEeY%3D"

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