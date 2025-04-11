package com.pinelabs.pluralsdk.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.DCCDetails
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.utils.AmountUtil
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.DCC_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class SuccessActivity : AppCompatActivity() {

    private val AUTO_CLOSE_DELAY = 2000L
    private lateinit var viewModel: FetchDataViewModel
    var orderId: String? = null
    private var dccAmountMessage: DCCDetails? = null

    private var txt_inr: TextView? = null
    private var txt_usd: TextView? = null
    private var txt_header: TextView? = null
    private var linear_dcc_detail: LinearLayout? = null
    private var layout_dcc_success: ConstraintLayout? = null

    private var txt_you_paid: TextView? = null
    private var txt_amount: TextView? = null
    private var txt_to: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_success)

        orderId = intent.getStringExtra(
            ORDER_ID
        )

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]

        setStatusBarColor(this, null)
        PluralSDK.getInstance().callback!!.onSuccessOccured(
            orderId
        )

        Handler().postDelayed({
            finish() // Close
        }, AUTO_CLOSE_DELAY)

        layout_dcc_success = findViewById(R.id.layout_dcc_success)
        txt_inr = findViewById(R.id.txt_inr)
        txt_usd = findViewById(R.id.txt_usd)
        txt_header = findViewById(R.id.txt_dcc_header_opt)
        linear_dcc_detail = findViewById(R.id.linear_dcc_details)
        txt_you_paid = findViewById(R.id.txt_you_paid)
        txt_amount = findViewById(R.id.txt_amount)
        txt_to = findViewById(R.id.txt_to)

        if (intent.getParcelableExtra<DCCDetails>(DCC_DATA) != null)
            dccAmountMessage = intent.getParcelableExtra<DCCDetails>(DCC_DATA)
        if (dccAmountMessage != null) {
            linear_dcc_detail?.visibility = View.VISIBLE
            if (dccAmountMessage?.foreginCurrencyAmount != null) {
                layout_dcc_success?.visibility = View.VISIBLE
                txt_header?.text =
                    txt_header?.text.toString() + " " + dccAmountMessage?.foreginCurrency + ":"
                txt_inr?.text =
                    txt_inr?.text.toString() + " " + AmountUtil.convertToRupees(
                        this,
                        dccAmountMessage?.nativeCurrencyAmount!!
                    )
                val foreignTransformation =
                    "1 " + dccAmountMessage?.foreginCurrencyLabel + "=" + AmountUtil.convertTransformation(
                        dccAmountMessage?.transformationRatio,
                        dccAmountMessage?.conversionRate
                    )
                val foreignValue =
                    dccAmountMessage?.foreginCurrencyLabel + " " + AmountUtil.transformAmount(
                        dccAmountMessage?.transformationRatio,
                        dccAmountMessage?.foreginCurrencyAmount
                    )
                txt_usd?.text =
                    dccAmountMessage?.foreginCurrency + " " + foreignValue + " " + foreignTransformation
                txt_amount?.text = " " + foreignValue
                txt_to?.text = " " + txt_to?.text.toString() + " " + dccAmountMessage?.merchantName
            } else {
                layout_dcc_success?.visibility = View.GONE
                val indianValue = AmountUtil.convertToRupees(
                    this,
                    dccAmountMessage?.nativeCurrencyAmount!!
                )
                txt_amount?.text = " " + indianValue
                txt_to?.text = " " + txt_to?.text.toString() + " " + dccAmountMessage?.merchantName
            }
        } else {
            linear_dcc_detail?.visibility = View.GONE
            layout_dcc_success?.visibility = View.GONE
        }

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

    private fun setStatusBarColor(context: Context, palette: Palette?) {
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.setStatusBarColor(color)
    }

}
