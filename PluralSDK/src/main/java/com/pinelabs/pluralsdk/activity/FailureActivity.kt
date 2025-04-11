package com.pinelabs.pluralsdk.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clevertap.android.sdk.CleverTapAPI
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.END_BOLD
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_CODE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE_DEFAULT
import com.pinelabs.pluralsdk.utils.Constants.Companion.FAILURE_TIMER
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPACE
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_BOLD
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class FailureActivity : AppCompatActivity() {

    private lateinit var error_message: String
    private lateinit var error_code: String
    private lateinit var viewModel: FetchDataViewModel

    private lateinit var txtRetry: TextView
    private lateinit var txtAutoClose: TextView

    var orderId: String? = null
    var paymentId: String? = null

    var clevertapDefaultInstance: CleverTapAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_failed)

        setStatusBarColor(this, null)

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]

        getIntentValues()
        getClevertapInstance()
        getViews()
        StartTimer()
        ObserveData()

    }

    private fun setStatusBarColor(context: Context, palette: Palette?) {
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.statusBarColor = color
    }

    private fun getClevertapInstance() {
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        CleverTapUtil.CT_EVENT_PAYMENT_STATUS_FAILURE(
            clevertapDefaultInstance,
            orderId,
            paymentId,
            error_code,
            error_message
        )
    }

    private fun getIntentValues() {
        orderId = intent.getStringExtra(ORDER_ID)
        paymentId = intent.getStringExtra(PAYMENT_ID)
        error_code = intent.getStringExtra(ERROR_CODE).toString()
        error_message = intent.getStringExtra(ERROR_MESSAGE).toString()
        if (error_message.isEmpty())
            error_message = ERROR_MESSAGE_DEFAULT
    }

    private fun getViews() {
        txtAutoClose = findViewById(R.id.txt_autoclose)
        txtRetry = findViewById(R.id.txt_retry)
    }

    private fun StartTimer() {
        val timer = object : CountDownTimer(FAILURE_TIMER, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val autoCloseString =
                    resources.getString(R.string.auto_close) + SPACE + START_BOLD + ((millisUntilFinished / 1000) + 1) + END_BOLD
                txtAutoClose.text = Html.fromHtml(autoCloseString)
            }

            override fun onFinish() {
                PluralSDK.getInstance().callback!!.onErrorOccured(
                    orderId,
                    error_code,
                    error_message
                )
                finish()
            }
        }
        timer.start()

        txtRetry.text = error_message
    }

    private fun ObserveData() {
        try {
            viewModel.fetch_data_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this, onLoading = {
                    }, onSuccess = { data ->
                        data?.merchantBrandingData?.palette?.let { palette ->
                            setStatusBarColor(this, palette)
                        }

                    }, onFailure = { errorMessage ->

                    })
                fetchDataResponseHandler.handleApiResult(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}