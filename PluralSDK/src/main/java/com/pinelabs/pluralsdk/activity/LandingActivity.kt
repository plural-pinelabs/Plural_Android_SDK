package com.pinelabs.pluralsdk.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.isNotNullAndBlank
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.CustomerInfoResponse
import com.pinelabs.pluralsdk.data.model.DCCDetails
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.SavedCardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.utils.AmountUtil.convertToRupees
import com.pinelabs.pluralsdk.utils.AmountUtil.roundToDecimal
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.fragment.ACSFragment
import com.pinelabs.pluralsdk.fragment.AddressFragment
import com.pinelabs.pluralsdk.fragment.BottomSheetMobileNumber
import com.pinelabs.pluralsdk.fragment.BottomSheetOtp
import com.pinelabs.pluralsdk.fragment.BottomSheetRetryFragment
import com.pinelabs.pluralsdk.fragment.CardFragment
import com.pinelabs.pluralsdk.fragment.NetBankingFragmentNew
import com.pinelabs.pluralsdk.fragment.OtpFragment
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing
import com.pinelabs.pluralsdk.fragment.SavedCardFragment
import com.pinelabs.pluralsdk.fragment.UPICollectFragment
import com.pinelabs.pluralsdk.fragment.WalletFragment
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.CleverTapUtil.Companion.CT_EVENT_PAYMENT_CANCELLED
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_DETAILS
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.DCC_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.EMAIL
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_CODE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_BOTTOM_SHEET_MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_BOTTOM_SHEET_OTP
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_PAYMENT_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_SAVED_CARD_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_STATUS
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.RetryViewModel
import com.pinelabs.pluralsdk.viewmodels.SavedCardViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactoryRetry
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactorySavedCard


class LandingActivity : AppCompatActivity(), Thread.UncaughtExceptionHandler,
    CardFragment.onRetryListener, NetBankingFragmentNew.onRetryListener,
    UPICollectFragment.onRetryListener,
    ACSFragment.onRetryListener, OtpFragment.onRetryListener, SavedCardFragment.onRetryListener,
    AddressFragment.onRetryListener, WalletFragment.onRetryListener {

    lateinit var customerLayout: FrameLayout
    lateinit var layoutOrginal: ConstraintLayout
    lateinit var layoutShimmer: View

    private lateinit var token: String

    private lateinit var imgMerchantimage: ImageView
    private lateinit var txtMerchantname: TextView
    private lateinit var txtTransactionamount: TextView
    private lateinit var txtTransactionamountStrike: TextView
    private lateinit var txtMerchantMobileNumber: TextView
    private lateinit var txtMerchantEmailId: TextView
    private lateinit var imgProfileEdit: ImageView
    private lateinit var cardProfilePic: CardView

    private lateinit var viewModel: FetchDataViewModel
    private lateinit var retryViewModel: RetryViewModel
    private lateinit var savedCardViewModel: SavedCardViewModel

    var deepLink: String? = null
    var paymentId: String? = null

    private var amount: Int? = null
    private var palette: Palette? = null

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var loadTime: Long? = null

    private var clevertapDefaultInstance: CleverTapAPI? = null

    var paymentModes: List<PaymentMode>? = mutableListOf()
    var isAcs = false
    var errorCode: String? = null
    var errorMessage: String? = null

    private var customerInfo: CustomerInfo? = null
    private var dccAmountMessage: DCCDetails? = null
    private var merchantName: String? = null

    private lateinit var bottomSheetDialogOtp: BottomSheetDialogFragment
    lateinit var bottomSheetDialogMobile: BottomSheetDialogFragment

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        startTime = System.currentTimeMillis()

        setStatusBarColor(this)

        val viewModelFactory = ViewModelFactory(application)
        val viewModelFactoryRetry = ViewModelFactoryRetry(application)
        val viewModelFactorySavedCard = ViewModelFactorySavedCard(application)

        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]
        retryViewModel = ViewModelProvider(this, viewModelFactoryRetry)[RetryViewModel::class.java]
        savedCardViewModel =
            ViewModelProvider(this, viewModelFactorySavedCard)[SavedCardViewModel::class.java]

        token = intent.getStringExtra(TOKEN).toString()

        initializeViews()
        fetchData(token)
        setupCancelAction()
        observerFetchData()
    }

    private fun initializeViews() {

        imgMerchantimage = findViewById(R.id.img_pic_header)
        txtMerchantname = findViewById(R.id.txt_merchant_name_orginal)
        txtTransactionamount = findViewById(R.id.txt_amount)
        txtTransactionamountStrike = findViewById(R.id.amount_strike)
        txtTransactionamountStrike.setPaintFlags(txtTransactionamountStrike.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        txtMerchantMobileNumber = findViewById(R.id.txt_customer_id)
        txtMerchantEmailId = findViewById(R.id.txt_customer_email)
        imgProfileEdit = findViewById(R.id.img_edit)
        customerLayout = findViewById(R.id.box_layout_orginal)
        cardProfilePic = findViewById(R.id.card_pic_orginal)

        layoutOrginal = findViewById(R.id.header_layout)
        layoutShimmer = findViewById(R.id.header_layout_shimmer)

        viewModel.pbpAmount.observe(this) { pbpAmount ->
            if (pbpAmount != null) {
                Utils.println("Transaction amount ${convertToRupees(this, pbpAmount)}")
                txtTransactionamount.text = convertToRupees(this, pbpAmount)
                txtTransactionamountStrike.text = convertToRupees(this, amount!!)
                txtTransactionamountStrike.visibility = View.VISIBLE
            } else {
                txtTransactionamount.text = convertToRupees(this, amount!!)
                txtTransactionamountStrike.visibility = View.GONE
            }
        }

        viewModel.dccAmount.observe(this) { dccAmount ->
            txtTransactionamount.text = dccAmount
            txtTransactionamountStrike.visibility = View.GONE
        }
        viewModel.dccAmountMessage.observe(this) { dccMessage ->
            if (dccMessage != null) {
                this.dccAmountMessage = dccMessage
                this.dccAmountMessage?.merchantName = merchantName
            }
        }
        showPaymentListingFragment()
    }

    private fun showPaymentListingFragment() {
        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        val paymentListingFragment = PaymentOptionListing()
        paymentListingFragment.arguments = arguments

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, paymentListingFragment, TAG_PAYMENT_LISTING)
        transaction.commit()
    }

    private fun showSavedCardFragment() {
        findViewById<LinearLayout>(R.id.saved_card_fragment).visibility = View.VISIBLE
        val savedCardFragment = SavedCardFragment(
            true,
            txtMerchantEmailId.text.toString(),
            txtMerchantMobileNumber.text.toString(),
            token
        )

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.saved_card_fragment, savedCardFragment, TAG_SAVED_CARD_LISTING)
        transaction.addToBackStack(TAG_SAVED_CARD_LISTING)
        transaction.commit()
    }

    private fun fetchData(token: String) {
        viewModel.fetchData(token)
    }

    private fun setupCancelAction() {
        val cancelLayout: View = findViewById(R.id.cancel_layout)
        val cancelShimmerlayout: View = findViewById(R.id.cancel_layout_shimmer)

        cancelShimmerlayout.setOnClickListener {
            clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, true, false) }
            showCancelConfirmationDialog(this, null)
        }

        cancelLayout.setOnClickListener {
            clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, true, false) }
            showCancelConfirmationDialog(this, null)
        }
    }

    fun showCancelConfirmationDialog(activity: Activity, tag: String?) {
        val bottomSheetDialog = BottomSheetDialog(activity)
        val view =
            LayoutInflater.from(activity).inflate(R.layout.cancel_confirmation_bottom_sheet, null)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(view)

        val btnYes: Button = view.findViewById(R.id.btn_yes)
        val btnNo: Button = view.findViewById(R.id.btn_no)

        if (palette != null) {
            btnYes.backgroundTintList = ColorStateList.valueOf(Color.parseColor(palette?.C900))
            btnNo.setTextColor(Color.parseColor(palette?.C900))
            val drawable =
                ContextCompat.getDrawable(activity, R.drawable.outlined_button) as GradientDrawable
            drawable.setStroke(convertDpToPx(2), Color.parseColor(palette?.C900))
            btnNo.background = drawable
        }

        btnYes.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (tag != null && !findViewById<LinearLayout>(R.id.saved_card_fragment).isVisible) {
                supportFragmentManager.popBackStack()
            } else {
                //Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
                activity.finish()
                PluralSDK.getInstance().callback?.onCancelTransaction()
            }
            viewModel.cancelTransaction(token, if (paymentId == null) false else true)
        }

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, false, true) }

        if (supportFragmentManager.backStackEntryCount > 0) {

            Utils.println(
                "Back presss ${supportFragmentManager.backStackEntryCount} ${
                    supportFragmentManager.getBackStackEntryAt(
                        supportFragmentManager.backStackEntryCount - 1
                    ).name
                }"
            )

            val tag =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name

            if (supportFragmentManager.backStackEntryCount == 2) {
                if (customerInfo?.tokens?.isNotEmpty() == true) {
                    findViewById<LinearLayout>(R.id.saved_card_fragment).visibility =
                        View.VISIBLE
                } else {
                    findViewById<LinearLayout>(R.id.saved_card_fragment).visibility =
                        View.GONE
                }
            }

            if (tag.equals(TAG_ACS))
                this.isAcs = true
            supportFragmentManager.findFragmentByTag(TAG_ACS)?.requireActivity()
                ?.findViewById<WebView>(R.id.web_acs)?.visibility = View.GONE
            supportFragmentManager.findFragmentByTag(TAG_ACS)?.requireActivity()
                ?.findViewById<ConstraintLayout>(R.id.constrain_success)?.visibility = View.VISIBLE
            /*if (tag.equals(TAG_UPI) && deepLink != null) {
                showCancelConfirmationDialog(this, TAG_UPI)
            } else if (tag.equals(TAG_ACS)) {
                this.isAcs = true
                viewModel.cancelTransaction(token)
            } else
                super.onBackPressed()*/
            if (tag.equals(TAG_ACS) /*|| tag.equals(TAG_OTP)*/) {
                onRetry(isAcs, "", "")
            } else if (deepLink.isNotNullAndBlank() || paymentId.isNotNullAndBlank() || (supportFragmentManager.backStackEntryCount < 2 && findViewById<LinearLayout>(
                    R.id.saved_card_fragment
                ).isVisible)
            )
                showCancelConfirmationDialog(this, tag)
            else
                supportFragmentManager.popBackStack()


        } else if (supportFragmentManager.backStackEntryCount == 0) {
            showCancelConfirmationDialog(this, null)
        } else {
            super.onBackPressed()
        }
    }


//    private fun isCurrentActivity(): Boolean {
//        return this@LandingActivity.javaClass.simpleName == "LandingActivity"
//    }

    fun observerFetchData() {
        viewModel.paymentId.observe(this) { response ->
            paymentId = response
        }
        retryViewModel.transaction_status_response.distinctUntilChanged()
            .observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<TransactionStatusResponse>(this, onLoading = {
                    }, onSuccess = { data ->
                        Utils.println("Transaction status landing")
                        data?.data?.let { data ->
                            //data.status = UPI_PROCESSED_STATUS
                            if (data.status.equals(UPI_PROCESSED_STATUS)) {
                                val intent = Intent(this, SuccessActivity::class.java)
                                intent.putExtra(ORDER_ID, data.order_id)
                                intent.putExtra(DCC_DATA, dccAmountMessage)
                                startActivity(intent)
                                finish()
                            } else {
                                if (data.is_retry_available) {
                                    val bottomSheetDialog =
                                        BottomSheetRetryFragment(
                                            txtTransactionamount.text.toString()
                                                .replace(Regex("\\s+"), ""),
                                            isAcs,
                                            paymentModes,
                                            palette,
                                            token,
                                            errorMessage
                                        )
                                    bottomSheetDialog.isCancelable = false
                                    bottomSheetDialog.show(supportFragmentManager, "")
                                    //mainViewModel.clearTransactionStatus()
                                } else {
                                    val intent = Intent(this, FailureActivity::class.java)
                                    //intent.putExtra(ORDER_ID, orderId)
                                    intent.putExtra(ERROR_CODE, errorCode)
                                    intent.putExtra(ERROR_MESSAGE, errorMessage)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        }
                    }, onFailure = { error ->
                        val intent = Intent(this, FailureActivity::class.java)
                        //intent.putExtra(ORDER_ID, orderId)
                        intent.putExtra(ERROR_CODE, error?.error_code)
                        intent.putExtra(ERROR_MESSAGE, error?.error_message)
                        startActivity(intent)
                        finish()
                        Utils.println("Transaction status failure")
                    })
                fetchDataResponseHandler.handleApiResult(response)

            }


        try {

            viewModel.saved_card_request_otp_response.observe(this) { response ->
                val responseHandler =
                    ApiResultHandler<SavedCardResponse>(this@LandingActivity, onLoading = {
                    }, onSuccess = { data ->

                        //if (::bottomSheetDialogMobile.isInitialized && bottomSheetDialogMobile.isVisible) bottomSheetDialogMobile.dismiss()
                        viewModel.otpId.value = data?.otpId
                        if (!::bottomSheetDialogOtp.isInitialized || !bottomSheetDialogOtp.isVisible) {
                            val argument = Bundle()
                            argument.putString(TOKEN, token)
                            argument.putString(MOBILE, customerInfo?.mobileNumber)
                            argument.putString(CUSTOMER_ID, customerInfo?.customer_id)
                            argument.putString(OTP_ID, data?.otpId)
                            argument.putParcelable(CUSTOMER_DETAILS, customerInfo)

                            bottomSheetDialogOtp = BottomSheetOtp(palette)
                            bottomSheetDialogOtp.arguments = argument
                            bottomSheetDialogOtp.isCancelable = true
                            bottomSheetDialogOtp.show(supportFragmentManager, TAG_BOTTOM_SHEET_OTP)

                            Utils.println("Otp response ${data?.otpId}}")
                        }
                    }, onFailure = { errorMessage ->
                        Utils.println("otp request error ${errorMessage?.error_message}")
                    })
                responseHandler.handleApiResult(response)

            }

            viewModel.saved_card_create_inactive_response.observe(this) { response ->
                val responseHandler =
                    ApiResultHandler<CustomerInfo>(this@LandingActivity, onLoading = {
                    }, onSuccess = { data ->
                        customerInfo?.customer_id = data?.customer_id
                        val otpRequest = OTPRequest(null, null, data?.customer_id, null, null)
                        viewModel.sendOTPCustomer(token, otpRequest)
                    }, onFailure = { errorMessage ->
                        Utils.println("Create active inactive error${errorMessage?.error_message}")
                    })
                responseHandler.handleApiResult(response)
            }

            viewModel.saved_card_validate_update_order_response.observe(this) { response ->
                val responseHandler =
                    ApiResultHandler<CustomerInfoResponse>(this@LandingActivity, onLoading = {
                    }, onSuccess = { data ->
                        if (data?.customerInfo != null) {
                            viewModel.mobileNumberValidate.value = true
                            if (::bottomSheetDialogMobile.isInitialized && bottomSheetDialogMobile.isVisible) bottomSheetDialogMobile.dismiss()
                            if (::bottomSheetDialogOtp.isInitialized && bottomSheetDialogOtp.isVisible) bottomSheetDialogOtp.dismiss()
                            customerInfo = data?.customerInfo
                            data?.customerInfo?.mobileNo.let { mobileNo ->
                                txtMerchantMobileNumber.visibility = View.VISIBLE
                                txtMerchantMobileNumber.text = mobileNo
                            }
                            data?.customerInfo?.emailId.let { email ->
                                txtMerchantEmailId.visibility = View.VISIBLE
                                txtMerchantEmailId.text = email
                            }

                            if (data?.customerInfo?.mobileNo != null && data?.customerInfo?.emailId != null) {
                                customerLayout.visibility = View.VISIBLE
                                findViewById<TextView>(R.id.seperator).visibility = View.VISIBLE
                            }

                            if (data?.customerInfo?.tokens?.isNotEmpty() == true) {
                                showSavedCardFragment()
                            } else {
                                findViewById<LinearLayout>(R.id.saved_card_fragment).visibility =
                                    View.GONE
                            }
                        } else {
                            viewModel.mobileNumberValidate.value = false
                            viewModel.savedCardOtpError.value = getString(R.string.wrong_otp)
                        }

                        Utils.println("Validate & update customer info ${data?.customerInfo?.tokens?.size}")
                    }, onFailure = { errorMessage ->
                        Utils.println("Validate & update customer info ${errorMessage?.error_message}")
                    })
                responseHandler.handleApiResult(response)
            }

            viewModel.fetch_data_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this@LandingActivity, onLoading = {
                        startShimmer()
                    }, onSuccess = { data ->
                        merchantName = data?.merchantInfo?.merchantName
                        //orderId = response.order_id
                        Utils.println("Fetch data " + Gson().toJson(data))
                        setView(data)

                        paymentModes = response.data?.paymentModes?.filter { paymentMode ->
                            paymentMode.paymentModeData != null || paymentMode.paymentModeId == PAYBYPOINTS_ID
                        }

                    }, onFailure = { errorMessage ->
                        val i = Intent(applicationContext, FailureActivity::class.java)
                        i.putExtra(ERROR_CODE, errorMessage?.error_code)
                        i.putExtra(ERROR_MESSAGE, errorMessage?.error_message)
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
                        this.paymentId = null
                        this.deepLink = null
                        /*if (this.isAcs) {
                            val bottomSheetDialog =
                                BottomSheetRetryFragment(
                                    convertToRupees(this, amount!!),
                                    isAcs,
                                    paymentModes,
                                    token
                                )
                            bottomSheetDialog.isCancelable = false
                            bottomSheetDialog.show(supportFragmentManager, "")
                        }*/ /*else {
                            Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
                            PluralSDK.getInstance().callback?.onCancelTransaction()
                            finish()
                        }*/
                    }, onFailure = {

                    })
                cancelTransactionResultHandler.handleApiResult(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setView(fetchResponse: FetchResponse?) {
        //constraintLayout = layoutOrginal.findViewById(R.id.constrain_layout)

        fetchResponse?.customerInfo?.let { info ->

            if (info.mobileNo != null || info.emailId != null)
                customerLayout.visibility = View.VISIBLE

            txtMerchantMobileNumber.text = info.mobileNo
            txtMerchantEmailId.text = info.emailId

            if (info?.tokens?.isNotEmpty() == true) {
                showSavedCardFragment()
            } else {
                findViewById<LinearLayout>(R.id.saved_card_fragment).visibility = View.GONE
            }

            if (info.isEditCustomerDetailsAllowed != null && info.isEditCustomerDetailsAllowed) {

                customerInfo = fetchResponse?.customerInfo
                customerInfo?.customer_id = fetchResponse?.customerInfo?.customerId
                customerInfo?.billingAddress = fetchResponse?.billingAddress
                customerInfo?.shippingAddress = fetchResponse?.shippingAddress

                findViewById<ImageView>(R.id.img_edit).let { edit ->
                    edit.visibility = View.VISIBLE
                    edit.setOnClickListener {
                        showMobileNumberPopup()
                    }
                }

            }

            if (info.mobileNo != null) {
                findViewById<TextView>(R.id.txt_customer_id).visibility = View.VISIBLE
            } else {
                if (info.isEditCustomerDetailsAllowed == true) {
                    showMobileNumberPopup()
                }
            }

            if (info.emailId != null) {
                findViewById<TextView>(R.id.txt_customer_email).visibility = View.VISIBLE
            }

            if (info.mobileNo != null && info.emailId != null) {
                findViewById<TextView>(R.id.seperator).visibility = View.VISIBLE
            }

        }
        /*fetchResponse?.merchantBrandingData = MerchantBranding(null, null, Palette("", "", "", "", "",
            "","","","","#0096FF"))*/

        if (fetchResponse?.merchantBrandingData == null || fetchResponse?.merchantBrandingData?.logo == null || fetchResponse?.merchantBrandingData?.logo?.imageContent.isNullOrEmpty()) {
            makePicInvisible()
            /*color = fetchResponse.merchantBrandingData!!.brandTheme.color
            Toast.makeText(this@LandingActivity, color, Toast.LENGTH_SHORT).show()
            println("Color>>>>"+color)
            println("Color Changed>>>>"+ColorUtil.generateTransparentColor(color, COLOR_ENUM.COLOR_20.colors))
            layoutOrginal.setBackgroundColor(ColorUtil.generateTransparentColor(color, COLOR_ENUM.COLOR_90.colors))*/
        } else {
            try {
                val content =
                    fetchResponse?.merchantBrandingData?.logo?.imageContent!!.split(",")[1]
                val bitmap = decodeBase64ToBitmap(content)
                imgMerchantimage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        fetchResponse?.merchantBrandingData?.palette?.let { palette ->
            this.palette = palette
            setColor()
            setStatusBarColor(this)
        }

        txtMerchantname.text = fetchResponse?.merchantInfo?.merchantDisplayName
        amount = fetchResponse?.paymentData?.originalTxnAmount?.amount!!
        val amountString = convertToRupees(this, amount!!)

        val spannable: Spannable = SpannableString(amountString)
        val end = amountString.length
        val start = end - (amountString.split(".")[1]).length

        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.shimmer_grey)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtTransactionamount.setText(spannable, TextView.BufferType.SPANNABLE)

        if (fetchResponse == null) Toast.makeText(this, "Fetch response null", Toast.LENGTH_SHORT)
            .show()

        if (fetchResponse.customerInfo == null) Toast.makeText(
            this,
            "Customer info null",
            Toast.LENGTH_SHORT
        ).show()

        /*Toast.makeText(
            this,
            "Mobile number ->${fetchResponse?.customerInfo?.mobileNo} Email id ->${fetchResponse?.customerInfo?.emailId}",
            Toast.LENGTH_SHORT
        ).show()*/

        endTime = System.currentTimeMillis()
        loadTime = endTime!! - startTime!!
        CleverTapUtil.CT_PROFILE(
            clevertapDefaultInstance,
            fetchResponse?.customerInfo?.emailId,
            fetchResponse?.customerInfo?.mobileNo
        )
        CleverTapUtil.CT_EVENT_PAYMENT_PAGE_LOADED(
            clevertapDefaultInstance, loadTime?.toInt(), fetchResponse?.merchantInfo?.merchantId,
            roundToDecimal(
                fetchResponse?.paymentData?.originalTxnAmount?.amount!!.toDouble() / 100
            ),
            fetchResponse?.customerInfo?.mobileNo, fetchResponse?.customerInfo?.emailId
        )
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

    private fun decodeBase64ToBitmap(base64String: String?): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun setColor() {
        layoutOrginal.backgroundTintList = ColorStateList.valueOf(Color.parseColor(palette?.C900))
        //window.statusBarColor = Color.parseColor(palette?.C900)
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun setStatusBarColor(context: Context) {
        val window = window
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        window.statusBarColor = color
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Utils.println("Exception caught")
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        CleverTapUtil.CT_EVENT_SDK_ERROR(
            clevertapDefaultInstance,
            e.javaClass.toString(),
            e.printStackTrace().toString()
        )
    }

    override fun onRetry(isAcs: Boolean, errorCode: String?, errorMessage: String?) {
        this.isAcs = isAcs
        this.errorCode = errorCode
        this.errorMessage = errorMessage
        retryViewModel.getTransactionStatus(token)
        //loadFragment(fragmentTag)
    }

    private fun showMobileNumberPopup() {
        val arguments = Bundle()
        arguments.putParcelable(CUSTOMER_DETAILS, customerInfo)
        arguments.putString(TOKEN, token)
        arguments.putString(MOBILE, txtMerchantMobileNumber.text.toString())
        arguments.putString(EMAIL, txtMerchantEmailId.text.toString())

        bottomSheetDialogMobile = BottomSheetMobileNumber(palette)
        bottomSheetDialogMobile.arguments = arguments

        bottomSheetDialogMobile.isCancelable = false
        bottomSheetDialogMobile.show(supportFragmentManager, TAG_BOTTOM_SHEET_MOBILE)
    }
}