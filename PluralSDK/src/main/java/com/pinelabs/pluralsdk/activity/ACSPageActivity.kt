package com.pinelabs.pluralsdk.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class ACSPageActivity : AppCompatActivity() {
    private lateinit var webAcs: WebView
    private lateinit var viewModel: FetchDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acs_webpage)

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

        try {
            viewModel.fetch_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this@ACSPageActivity, onLoading = {
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

        /*webAcs.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                println("URL $url")
                if (url!!.contains(SUCCESS_REDIRECT_URL)) {

                }
            }
        }*/
        //webAcs.loadUrl("file:///android_asset/x.html");
        webAcs.addJavascriptInterface(WebAppInterface(this@ACSPageActivity), "AndroidInterface")
    }

    class WebAppInterface(context: Activity) {
        private val mContext: Activity = context

        @JavascriptInterface
        fun postMessage(response: String?) {
            println("Response from java script ${response}")
            val intent = Intent(mContext, SuccessActivity::class.java)
            mContext.startActivity(intent)
            mContext.finish()
        }
    }

    private fun setStatusBarColor(context: Context, palette: Palette) {
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.setStatusBarColor(color)
    }
}