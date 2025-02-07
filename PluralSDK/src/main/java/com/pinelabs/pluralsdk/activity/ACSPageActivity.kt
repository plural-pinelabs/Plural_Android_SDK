package com.pinelabs.pluralsdk.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_STATUS
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class ACSPageActivity : AppCompatActivity() {
    private lateinit var webAcs: WebView
    private lateinit var viewModel: FetchDataViewModel

    var orderId: String? = null
    var paymentId: String? = null
    var startTime: Int? = null
    var token: String? = null

    var paymentModes: List<PaymentMode>? = mutableListOf()

    private var clevertapDefaultInstance: CleverTapAPI? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var listener: OnButtonClickListener? = null

    interface OnButtonClickListener {
        fun onFragmentButtonClick(fragmentTag: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acs_webpage)

        listener = this as? OnButtonClickListener

        orderId = intent.getStringExtra(ORDER_ID)
        paymentId = intent.getStringExtra(PAYMENT_ID)
        startTime = intent.getIntExtra(START_TIME, 0)
        token = intent.getStringExtra(TOKEN)

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this@ACSPageActivity)
        val endTime = System.currentTimeMillis()
        val processTime = endTime - startTime!!
        clevertapDefaultInstance?.let {
            CleverTapUtil.CT_EVENT_PAYMENT_COMPLETION_TIME(
                it,
                processTime
            )
        }

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]

        webAcs = findViewById(R.id.web_acs)
        webAcs.settings.javaScriptEnabled = true
        webAcs.settings.loadWithOverviewMode = true
        webAcs.settings.useWideViewPort = true
        webAcs.settings.setSupportZoom(true)
        webAcs.settings.builtInZoomControls = true
        webAcs.settings.javaScriptCanOpenWindowsAutomatically = true
        webAcs.settings.displayZoomControls = false
        webAcs.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webAcs.clearCache(true)
        webAcs.clearHistory()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webAcs.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webAcs.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webAcs.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return false
            }
        })

        webAcs.loadUrl(intent!!.getStringExtra(REDIRECT_URL).toString())

        viewModel.fetchData(token)
        try {
            viewModel.fetch_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this@ACSPageActivity, onLoading = {
                    }, onSuccess = { data ->
                        data?.merchantBrandingData?.palette?.let { palette ->
                            setStatusBarColor(this, palette)
                        }

                        paymentModes = response.data?.paymentModes?.filter { paymentMode ->
                            paymentMode.paymentModeData != null || paymentMode.paymentModeId == PAYBYPOINTS_ID
                        }
                        retryPopup(this@ACSPageActivity)

                    }, onFailure = { errorMessage ->
                        println("Failure")
                    })
                fetchDataResponseHandler.handleApiResult(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        webAcs.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                println("URL $url")
                if (url!!.contains("responseHandler")) {
                    //viewModel.getTransactionStatus(token)
                    finish()
                    listener?.onFragmentButtonClick("")
                }
            }
        }
        //webAcs.loadUrl("file:///android_asset/x.html");
        webAcs.addJavascriptInterface(WebAppInterface(this@ACSPageActivity), "AndroidInterface")

        viewModel.transaction_status_response.observe(this) { response ->
            val fetchDataResponseHandler =
                ApiResultHandler<TransactionStatusResponse>(this@ACSPageActivity, onLoading = {
                }, onSuccess = { data ->
                    println("Is retry available " + data?.data?.is_retry_available)
                    data?.data?.let {
                        if (it.status != UPI_PROCESSED_STATUS && it.is_retry_available) {

                        } else {
                            val intent = Intent(this, SuccessActivity::class.java)
                            intent.putExtra(ORDER_ID, orderId)
                            startActivity(intent)
                            finish()
                        }
                    }
                }, onFailure = {
                })
            fetchDataResponseHandler.handleApiResult(response)

        }

    }

    inner class WebAppInterface(context: Activity) {
        private val mContext: Activity = context

        @JavascriptInterface
        fun postMessage(response: String?) {
            println("Response from java script ${response}")

            CleverTapUtil.CT_EVENT_PAYMENT_STATUS_SUCCESS(
                clevertapDefaultInstance, orderId, paymentId
            )

        }
    }

    private fun setStatusBarColor(context: Context, palette: Palette) {
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.setStatusBarColor(color)
    }

    private fun retryPopup(context: Activity) {
       /* val bottomSheetDialog = BottomSheetRetryFragment(true, paymentModes, token)
        bottomSheetDialog.show(supportFragmentManager, "ModalBottomSheet")*/
        /*bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(this).inflate(R.layout.retry, null)
        var recyclerPaymentOptions: RecyclerView = view.findViewById(R.id.recycler_payment_options)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val myRecyclerViewAdapter = PaymentOptionsAdapter(
            mapPaymentModes(paymentModes!!), mapPaymentOptions(
                paymentModes!!
            ), null, this
        )
        recyclerPaymentOptions.adapter = myRecyclerViewAdapter
        recyclerPaymentOptions.layoutManager = layoutManager
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(
            ContextCompat.getDrawable(context, R.drawable.divider)!!
        )
        recyclerPaymentOptions.addItemDecoration(dividerItemDecoration)
        myRecyclerViewAdapter.notifyDataSetChanged()
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()*/
    }


}