package com.pinelabs.pluralsdk.activity

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.SUCCESS_REDIRECT_URL

class ACSPageActivity : AppCompatActivity() {

    private lateinit var webAcs: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acs_webpage)

        webAcs = findViewById(R.id.web_acs)

        webAcs.loadUrl(intent!!.getStringExtra(REDIRECT_URL).toString())
        webAcs.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url!!.contains(SUCCESS_REDIRECT_URL)){

                }
            }
        }

    }
    public fun setUpWebview(){
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
    }
}