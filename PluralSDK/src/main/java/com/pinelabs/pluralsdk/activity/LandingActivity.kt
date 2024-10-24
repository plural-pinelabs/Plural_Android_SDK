package com.pinelabs.pluralsdk.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
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
        setupCancelAction()
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

        showPaymentListingFragment()
    }

    fun showPaymentListingFragment() {
        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        val paymentListingFragment = PaymentOptionListing()
        paymentListingFragment.arguments = arguments

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, paymentListingFragment)
        transaction.commit()
    }

    fun fetchData(token:String){
        viewModel.fetchData(token)
    }

    private fun setupCancelAction() {
        val cancelLayout: View = findViewById(R.id.cancel_layout)
        val cancelShimmerlayout: View = findViewById(R.id.cancel_layout_shimmer)

        cancelShimmerlayout.setOnClickListener {
            showCancelConfirmationDialog() // Show popup from cross button
        }

        cancelLayout.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }


    private fun showCancelConfirmationDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.cancel_confirmation_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val btnYes: Button = view.findViewById(R.id.btn_yes)
        val btnNo: Button = view.findViewById(R.id.btn_no)

        btnYes.setOnClickListener {
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if the user confirms cancellation
        }

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    // Override onBackPressed to show the confirmation popup on back press
    override fun onBackPressed() {
        if (isCurrentActivity()) {
            showCancelConfirmationDialog() // Show popup from native back button press
        } else {
            super.onBackPressed() // Proceed with normal back press for other activities
        }
    }

    // Function to check if the user is on LandingActivity
    private fun isCurrentActivity(): Boolean {
        return this@LandingActivity.javaClass.simpleName == "LandingActivity"
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
                        val i = Intent(applicationContext, FailureActivity::class.java)
                        i.putExtra(ERROR_MESSAGE, errorMessage)
                        startActivity(i)
                        finish()
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