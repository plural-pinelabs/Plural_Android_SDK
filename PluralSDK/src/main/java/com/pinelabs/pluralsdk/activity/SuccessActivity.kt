package com.pinelabs.pluralsdk.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class SuccessActivity : AppCompatActivity() {

    private val AUTO_CLOSE_DELAY = 2000L
    private lateinit var viewModel: FetchDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_success)

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]

        PluralSDK.getInstance().callback!!.onSuccessOccured()

        Handler().postDelayed({
            finish() // Close
        }, AUTO_CLOSE_DELAY)

        try {
            viewModel.fetch_response.observe(this) { response ->
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

    private fun setStatusBarColor(context: Context, palette: Palette) {
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.setStatusBarColor(color)
    }

}
