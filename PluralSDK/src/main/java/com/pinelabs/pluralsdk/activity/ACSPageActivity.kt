package com.pinelabs.pluralsdk.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.SUCCESS_REDIRECT_URL
import org.json.JSONObject


class ACSPageActivity : AppCompatActivity() {
    private lateinit var webAcs: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acs_webpage)

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
        webAcs.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                println("URL $url")
                if (url!!.contains(SUCCESS_REDIRECT_URL)){
                    val intent = Intent(this@ACSPageActivity, SuccessActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
       // webAcs.addJavascriptInterface(this,"AndroidListener")


    }

     /*class WebAppInterface(var mContext: Context) {*/
        @JavascriptInterface
        fun message(response:String) {
            println("Response from java script ${response}")
            //Toast.makeText(mContext, response, Toast.LENGTH_SHORT ).show()
        }
    /*}*//**/
}