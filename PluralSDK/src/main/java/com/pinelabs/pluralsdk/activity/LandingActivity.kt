package com.pinelabs.pluralsdk.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory

class LandingActivity : AppCompatActivity() {

    lateinit var layoutOrginal: View
    lateinit var layoutShimmer: View

    private lateinit var token : String

    private lateinit var imgMerchantimage: ImageView
    private lateinit var txtMerchantname: TextView
    private lateinit var txtTransactionamount: TextView
    private lateinit var txtMerchantMobileNumber: TextView
    private lateinit var txtMerchantEmailId: TextView

    private lateinit var viewModel: FetchDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        val viewModelFactory= ViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[FetchDataViewModel::class.java]

        token = intent.getStringExtra(TOKEN).toString()

        getViews()
        fetchData(token)
        observerFetchData()
    }

    fun getViews() {
        imgMerchantimage = findViewById(R.id.img_pic)
        txtMerchantname = findViewById(R.id.txt_merchant_name)
        txtTransactionamount = findViewById(R.id.txt_amount)
        txtMerchantMobileNumber = findViewById(R.id.txt_customer_id)
        txtMerchantEmailId = findViewById(R.id.txt_customer_email)

        layoutOrginal = findViewById(R.id.layout_orginal)
        layoutShimmer = findViewById(R.id.layout_shimmer)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, PaymentOptionListing())
        transaction.commit()
    }

    fun fetchData(token:String){
        viewModel.fetchData(token)
    }

    fun observerFetchData() {
        try {
            viewModel.fetch_response.observe(this) { response ->
                val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(this@LandingActivity,
                    onLoading = {
                        startShimmer()
                    }, onSuccess = { data ->
                        setView(data)
                    }, onFailure = { errorMessage ->

                    })
                fetchDataResponseHandler.handleApiResult(response)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun setView(fetchResponse: FetchResponse?) {

        if (fetchResponse!!.merchantBrandingData!=null) {
            val imageBytes = Base64.decode(fetchResponse.merchantBrandingData!!.logo.imageContent, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imgMerchantimage.setImageBitmap(decodedImage)
        }

        txtMerchantname.text = fetchResponse.merchantInfo!!.merchantName
        txtTransactionamount.text = getString(R.string.rs)+fetchResponse.paymentData!!.originalTxnAmount.amount
        txtMerchantMobileNumber.text = fetchResponse.customerInfo!!.mobileNo
        txtMerchantEmailId.text = fetchResponse.customerInfo!!.emailId

        stopShimmer()
    }

    fun startShimmer() {
        layoutShimmer.isVisible = true
        layoutOrginal.isVisible = false
    }

    fun stopShimmer() {
        layoutShimmer.isVisible = false
        layoutOrginal.isVisible = true
    }

}