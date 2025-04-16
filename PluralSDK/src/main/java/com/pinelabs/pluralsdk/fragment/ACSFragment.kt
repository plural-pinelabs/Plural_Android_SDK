package com.pinelabs.pluralsdk.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieAnimationView
import com.clevertap.android.sdk.CleverTapAPI
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.data.utils.Utils.cleverTapLog
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel


class ACSFragment : Fragment() {

    private var clevertapDefaultInstance: CleverTapAPI? = null
    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    var orderId: String? = null
    var paymentId: String? = null
    var startTime: Long? = null
    var token: String? = null
    var redirectUrl: String? = null

    private lateinit var webAcs: WebView
    private lateinit var constrainSuccess: ConstraintLayout
    private var listener: onRetryListener? = null
    private lateinit var logoAnimation: LottieAnimationView

    interface onRetryListener {
        fun onRetry(isAcs: Boolean, errorCode:String?, errorMessage:String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? onRetryListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.acs_webpage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireActivity().supportFragmentManager.popBackStack()

        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.header_layout)
        activityButton.visibility = View.GONE

        orderId = arguments?.getString(ORDER_ID)
        paymentId = arguments?.getString(PAYMENT_ID)
        startTime = arguments?.getLong(START_TIME, 0)
        token = arguments?.getString(TOKEN)
        redirectUrl = arguments?.getString(REDIRECT_URL)

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        cleverTapLog()
        val endTime = System.currentTimeMillis()
        val processTime = endTime - startTime!!
        clevertapDefaultInstance?.let {
            CleverTapUtil.CT_EVENT_PAYMENT_COMPLETION_TIME(
                it,
                processTime
            )
        }

        constrainSuccess = view.findViewById(R.id.constrain_success)
        logoAnimation = view.findViewById(R.id.img_success_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

        webAcs = view.findViewById(R.id.web_acs)
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

        webAcs.loadUrl(redirectUrl!!)

        //mainViewModel.fetchData(token)
        try {
            mainViewModel.fetch_data_response
                .observe(viewLifecycleOwner) { response ->
                    val fetchDataResponseHandler =
                        ApiResultHandler<FetchResponse>(requireActivity(), onLoading = {
                        }, onSuccess = { data ->

                        }, onFailure = { errorMessage ->
                            Utils.println("Failure")
                        })
                    fetchDataResponseHandler.handleApiResult(response)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        webAcs.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Utils.println("URL $url")
                ("URL $url")
                if (url!!.contains("checkout-bff/responseHandler", ignoreCase = true)) {
                    CleverTapUtil.CT_EVENT_PAYMENT_STATUS_SUCCESS(
                        clevertapDefaultInstance, orderId, paymentId
                    )
                    //viewModel.getTransactionStatus(token)
                    webAcs.visibility = View.GONE
                    constrainSuccess.visibility = View.VISIBLE
                    listener?.onRetry(true,"", "")
                }
            }
        }
        //webAcs.loadUrl("file:///android_asset/x.html");
        webAcs.addJavascriptInterface(WebAppInterface(requireActivity()), "AndroidInterface")

        /*mainViewModel.transaction_status_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<TransactionStatusResponse>(requireActivity(), onLoading = {
                    }, onSuccess = { data ->
                        println("Is retry available " + data?.data?.is_retry_available)
                        data?.data?.let {
                            if (it.status != UPI_PROCESSED_STATUS && it.is_retry_available) {
                            } else {
                                val intent = Intent(requireActivity(), SuccessActivity::class.java)
                                intent.putExtra(ORDER_ID, orderId)
                                startActivity(intent)
                            }
                        }
                    }, onFailure = {
                    })
                fetchDataResponseHandler.handleApiResult(response)

            }*/

    }

    inner class WebAppInterface(context: Activity) {
        private val mContext: Activity = context

        @JavascriptInterface
        fun postMessage(response: String?) {
            Utils.println("Response from java script ${response}")

            CleverTapUtil.CT_EVENT_PAYMENT_STATUS_SUCCESS(
                clevertapDefaultInstance, orderId, paymentId
            )
            //viewModel.getTransactionStatus(token)
            webAcs.visibility = View.GONE
            constrainSuccess.visibility = View.VISIBLE
            listener?.onRetry(true,"", "")

        }
    }

    fun showProcessPayment(){
        webAcs.visibility = View.GONE
        constrainSuccess.visibility = View.VISIBLE
    }

}