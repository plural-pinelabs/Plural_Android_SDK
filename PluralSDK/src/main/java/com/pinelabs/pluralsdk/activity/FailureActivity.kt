package com.pinelabs.pluralsdk.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.END_BOLD
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE_DEFAULT
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
        if (error_message.isEmpty())
            error_message = ERROR_MESSAGE_DEFAULT

        txtAutoClose = findViewById(R.id.txt_autoclose)
        txtRetry = findViewById(R.id.txt_retry)
        val timer = object: CountDownTimer(FAILURE_TIMER, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val autoCloseString = resources.getString(R.string.auto_close)+SPACE+START_BOLD+ ((millisUntilFinished / 1000)+1)+ END_BOLD
                txtAutoClose.text = Html.fromHtml(autoCloseString)
            }

            override fun onFinish() {
                PluralSDK.getInstance().callback!!.onErrorOccured(error_message)
                finish()
            }
        }
        timer.start()

        txtRetry.text = error_message

    }
}