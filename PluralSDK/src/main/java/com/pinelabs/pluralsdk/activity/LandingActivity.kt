package com.pinelabs.pluralsdk.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.utils.AmountUtil.convertToRupees
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_PAYMENT_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory


class LandingActivity : AppCompatActivity() {

    lateinit var layoutOrginal: View
    lateinit var layoutShimmer: View

    private lateinit var token: String
    private lateinit var color: String

    private lateinit var imgMerchantimage: ImageView
    private lateinit var txtMerchantname: TextView
    private lateinit var txtTransactionamount: TextView
    private lateinit var txtTransactionamountStrike: TextView
    private lateinit var txtMerchantMobileNumber: TextView
    private lateinit var txtMerchantEmailId: TextView
    private lateinit var cardProfilePic: CardView

    private lateinit var viewModel: FetchDataViewModel

    var deepLink: String? = null

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var amount: Int? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]

        token = intent.getStringExtra(TOKEN).toString()
        bottomSheetDialog = BottomSheetDialog(this)

        getViews()
        fetchData(token)
        setupCancelAction()
        observerFetchData()
    }

    fun getViews() {

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.header_color)
        }

        imgMerchantimage = findViewById(R.id.img_pic)
        txtMerchantname = findViewById(R.id.txt_merchant_name_orginal)
        txtTransactionamount = findViewById(R.id.txt_amount)
        txtTransactionamountStrike = findViewById(R.id.amount_strike)
        txtTransactionamountStrike.setPaintFlags(txtTransactionamountStrike.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        txtMerchantMobileNumber = findViewById(R.id.txt_customer_id)
        txtMerchantEmailId = findViewById(R.id.txt_customer_email)
        cardProfilePic = findViewById(R.id.card_pic_orginal)

        layoutOrginal = findViewById(R.id.layout_orginal)
        layoutShimmer = findViewById(R.id.layout_shimmer)

        viewModel.pbpAmount.observe(this) { pbpAmount ->
            if (pbpAmount != null) {
                println("Transaction amount ${convertToRupees(this, pbpAmount)}")
                txtTransactionamount.text =  convertToRupees(this, pbpAmount)
                txtTransactionamountStrike.text =  convertToRupees(this, amount!!)
                txtTransactionamountStrike.visibility = View.VISIBLE
            } else {
                txtTransactionamount.text =  convertToRupees(this, amount!!)
                txtTransactionamountStrike.visibility = View.GONE
            }
        }
        showPaymentListingFragment()
    }

    fun showPaymentListingFragment() {
        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        val paymentListingFragment = PaymentOptionListing()
        paymentListingFragment.arguments = arguments

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, paymentListingFragment, TAG_PAYMENT_LISTING)
        transaction.commit()
    }

    fun fetchData(token: String) {
        viewModel.fetchData(token)
    }

    private fun setupCancelAction() {
        val cancelLayout: View = findViewById(R.id.cancel_layout)
        val cancelShimmerlayout: View = findViewById(R.id.cancel_layout_shimmer)

        cancelShimmerlayout.setOnClickListener {
            showCancelConfirmationDialog(null)
        }

        cancelLayout.setOnClickListener {
            showCancelConfirmationDialog(null)
        }
    }

    private fun showCancelConfirmationDialog(tag: String?) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view =
            LayoutInflater.from(this).inflate(R.layout.cancel_confirmation_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val btnYes: Button = view.findViewById(R.id.btn_yes)
        val btnNo: Button = view.findViewById(R.id.btn_no)

        btnYes.setOnClickListener {
            if (tag != null) {
                viewModel.cancelTransaction(token)
            } else {
                bottomSheetDialog.dismiss()
                Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 && supportFragmentManager.fragments[0].tag.equals(
                TAG_UPI
            )
        ) {
            showCancelConfirmationDialog(TAG_UPI)
        } else if (supportFragmentManager.backStackEntryCount == 0) {
            showCancelConfirmationDialog(null)
        } else {
            super.onBackPressed()
        }
    }


//    private fun isCurrentActivity(): Boolean {
//        return this@LandingActivity.javaClass.simpleName == "LandingActivity"
//    }

    fun observerFetchData() {
        try {
            viewModel.fetch_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this@LandingActivity, onLoading = {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            viewModel.cancel_transaction_response.observe(this) { response ->
                val cancelTransactionResultHandler =
                    ApiResultHandler<CancelTransactionResponse>(this@LandingActivity, onLoading = {

                    }, onSuccess = {
                        if (bottomSheetDialog != null && bottomSheetDialog.isShowing)
                            bottomSheetDialog.dismiss()
                        Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
                        PluralSDK.getInstance().callback?.onCancelTransaction()
                        finish()
                    }, onFailure = {

                    })
                cancelTransactionResultHandler.handleApiResult(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setView(fetchResponse: FetchResponse?) {
        if (fetchResponse?.merchantBrandingData == null || fetchResponse.merchantBrandingData!!.logo == null || fetchResponse.merchantBrandingData!!.logo.imageContent == null) {
            makePicInvisible()/*val imageBytes = Base64.decode(
                fetchResponse.merchantBrandingData!!.logo.imageContent,
                Base64.DEFAULT
            )
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imgMerchantimage.setImageBitmap(decodedImage)*/

            /*color = fetchResponse.merchantBrandingData!!.brandTheme.color
            Toast.makeText(this@LandingActivity, color, Toast.LENGTH_SHORT).show()
            println("Color>>>>"+color)
            println("Color Changed>>>>"+ColorUtil.generateTransparentColor(color, COLOR_ENUM.COLOR_20.colors))
            layoutOrginal.setBackgroundColor(ColorUtil.generateTransparentColor(color, COLOR_ENUM.COLOR_90.colors))*/
        }

        txtMerchantname.text = fetchResponse?.merchantInfo?.merchantName
        amount = fetchResponse?.paymentData?.originalTxnAmount?.amount!!
        val amountString = convertToRupees(this, amount!!)

        val spannable: Spannable = SpannableString(amountString)
        val start = amountString.indexOf(amountString.split(".")[1])
        val end = amountString.length
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.shimmer_grey)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtTransactionamount.setText(spannable, TextView.BufferType.SPANNABLE)

        txtMerchantMobileNumber.text = fetchResponse?.customerInfo?.mobileNo
        txtMerchantEmailId.text = fetchResponse?.customerInfo?.emailId

        if (fetchResponse == null) Toast.makeText(this, "Fetch response null", Toast.LENGTH_SHORT)
            .show()

        if (fetchResponse.customerInfo == null) Toast.makeText(
            this,
            "Customer info null",
            Toast.LENGTH_SHORT
        ).show()

        Toast.makeText(
            this,
            "Mobile number ->${fetchResponse?.customerInfo?.mobileNo} Email id ->${fetchResponse?.customerInfo?.emailId}",
            Toast.LENGTH_SHORT
        ).show()

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

    fun makePicInvisible() {
        cardProfilePic.layoutParams.width = 1
        cardProfilePic.visibility = View.INVISIBLE
        cardProfilePic.requestLayout()
        txtMerchantname.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = 0
        }
        txtMerchantname.requestLayout()
    }

}