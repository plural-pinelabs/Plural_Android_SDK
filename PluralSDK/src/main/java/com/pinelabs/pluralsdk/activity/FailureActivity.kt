package com.pinelabs.pluralsdk.activity

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.END_BOLD
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.FAILURE_TIMER
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPACE
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_BOLD
import java.util.Timer
import java.util.TimerTask

class FailureActivity : AppCompatActivity() {

    private lateinit var error_message : String

    private lateinit var txtRetry : TextView
    private lateinit var txtAutoClose : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_failed)

        error_message = intent.getStringExtra(ERROR_MESSAGE).toString()

        txtAutoClose = findViewById(R.id.txt_autoclose)
        txtRetry = findViewById(R.id.txt_retry)
        val autoCloseString = resources.getString(R.string.auto_close)+SPACE+START_BOLD+resources.getString(R.string.auto_close_time)+ END_BOLD
        txtAutoClose.text = Html.fromHtml(autoCloseString)
        txtRetry.text = error_message

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    PluralSDK.getInstance().callback!!.onErrorOccured(error_message)
                    finish()
                }
            }
        }, FAILURE_TIMER)

    }
}