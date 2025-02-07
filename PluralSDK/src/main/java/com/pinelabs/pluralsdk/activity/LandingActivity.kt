package com.pinelabs.pluralsdk.activity

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.utils.AmountUtil.convertToRupees
import com.pinelabs.pluralsdk.data.utils.AmountUtil.roundToDecimal
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.fragment.ACSFragment
import com.pinelabs.pluralsdk.fragment.BottomSheetRetryFragment
import com.pinelabs.pluralsdk.fragment.BottomSheetRetryUpiFragment
import com.pinelabs.pluralsdk.fragment.CardFragment
import com.pinelabs.pluralsdk.fragment.NetBankingFragment
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing
import com.pinelabs.pluralsdk.fragment.UPICollectFragment
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.CleverTapUtil.Companion.CT_EVENT_PAYMENT_CANCELLED
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_CODE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.REQ_RETRY_CALLBACK
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_NETBANKING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_PAYMENT_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_STATUS
import com.pinelabs.pluralsdk.utils.PaymentModes
import com.pinelabs.pluralsdk.utils.SmsBroadcastReceiver
import com.pinelabs.pluralsdk.utils.SmsBroadcastReceiver.SmsBroadcastReceiverListener
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.RetryViewModel
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactory
import com.pinelabs.pluralsdk.viewmodels.ViewModelFactoryRetry
import java.util.regex.Matcher
import java.util.regex.Pattern


class LandingActivity : AppCompatActivity(), Thread.UncaughtExceptionHandler,
    CardFragment.onRetryListener, NetBankingFragment.onRetryListener,
    UPICollectFragment.onRetryListener,
    ACSFragment.onRetryListener {

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
    private lateinit var cardProfilePic: CardView

    private lateinit var viewModel: FetchDataViewModel
    private lateinit var retryViewModel: RetryViewModel

    var deepLink: String? = null
    var paymentId: String? = null

    //private lateinit var window: Window

    private var amount: Int? = null
    private var palette: Palette? = null

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var loadTime: Long? = null

    private var clevertapDefaultInstance: CleverTapAPI? = null

    val REQ_USER_CONSENT: Int = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    var paymentModes: List<PaymentMode>? = mutableListOf()
    var isAcs = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)


        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        startTime = System.currentTimeMillis()

        val appSignatureHelper = AppSignatureHelper(this)
        println("Signature ${appSignatureHelper.appSignatures[0]}")

        startSmartUserConsent()

        val viewModelFactory = ViewModelFactory(application)
        val viewModelFactoryRetry = ViewModelFactoryRetry(application)

        viewModel = ViewModelProvider(this, viewModelFactory)[FetchDataViewModel::class.java]
        retryViewModel = ViewModelProvider(this, viewModelFactoryRetry)[RetryViewModel::class.java]

        token = intent.getStringExtra(TOKEN).toString()

        getViews()
        fetchData(token)
        setupCancelAction()
        observerFetchData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQ_USER_CONSENT) {
            if ((resultCode === RESULT_OK) && (data != null)) {
                val message: String? = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }

        /*Toast.makeText(this, "Callback reached", Toast.LENGTH_SHORT).show()
        if (requestCode === REQ_RETRY_CALLBACK) {
            if ((resultCode === RESULT_OK) && (data != null)) {
                val retryPage: String? = data?.getStringExtra(RETRY_PAGE)
                println("Retry page ${retryPage}")
                showPaymentListingFragment(retryPage)
            }
        }*/

    }

    fun getViews() {

        imgMerchantimage = findViewById(R.id.img_pic_header)
        txtMerchantname = findViewById(R.id.txt_merchant_name_orginal)
        txtTransactionamount = findViewById(R.id.txt_amount)
        txtTransactionamountStrike = findViewById(R.id.amount_strike)
        txtTransactionamountStrike.setPaintFlags(txtTransactionamountStrike.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        txtMerchantMobileNumber = findViewById(R.id.txt_customer_id)
        txtMerchantEmailId = findViewById(R.id.txt_customer_email)
        customerLayout = findViewById(R.id.box_layout_orginal)
        cardProfilePic = findViewById(R.id.card_pic_orginal)

        layoutOrginal = findViewById(R.id.layout_orginal)
        layoutShimmer = findViewById(R.id.layout_shimmer)

        viewModel.pbpAmount.observe(this) { pbpAmount ->
            if (pbpAmount != null) {
                println("Transaction amount ${convertToRupees(this, pbpAmount)}")
                txtTransactionamount.text = convertToRupees(this, pbpAmount)
                txtTransactionamountStrike.text = convertToRupees(this, amount!!)
                txtTransactionamountStrike.visibility = View.VISIBLE
            } else {
                txtTransactionamount.text = convertToRupees(this, amount!!)
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
            clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, true, false) }
            showCancelConfirmationDialog(this, null)
        }

        cancelLayout.setOnClickListener {
            clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, true, false) }
            showCancelConfirmationDialog(this, null)
        }
    }

    public fun showCancelConfirmationDialog(activity: Activity, tag: String?) {
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
            if (tag != null) {
                supportFragmentManager.popBackStack()
                viewModel.cancelTransaction(token)
            } else {
                //Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show()
                activity.finish()
                PluralSDK.getInstance().callback?.onCancelTransaction()
            }
        }

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, false, true) }

        println(
            "Back presss ${supportFragmentManager.backStackEntryCount} ${
                supportFragmentManager.getBackStackEntryAt(
                    supportFragmentManager.backStackEntryCount - 1
                ).name
            }"
        )

        if (supportFragmentManager.backStackEntryCount > 0) {
            val tag =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name

            if (tag.equals(TAG_ACS))
                this.isAcs = true

            /*if (tag.equals(TAG_UPI) && deepLink != null) {
                showCancelConfirmationDialog(this, TAG_UPI)
            } else if (tag.equals(TAG_ACS)) {
                this.isAcs = true
                viewModel.cancelTransaction(token)
            } else
                super.onBackPressed()*/

            if (tag.equals(TAG_ACS) || deepLink.isNotNullAndBlank() || paymentId.isNotNullAndBlank())
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
        retryViewModel.transaction_status_response.distinctUntilChanged()
            .observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<TransactionStatusResponse>(this, onLoading = {
                    }, onSuccess = { data ->
                        println("Transaction status landing")
                        data?.data?.let { data ->
                            if (data.status.equals(UPI_PROCESSED_STATUS)) {
                                val intent = Intent(this, SuccessActivity::class.java)
                                intent.putExtra(ORDER_ID, data?.order_id)
                                startActivity(intent)
                                finish()
                            } else {
                                if (data.is_retry_available) {
                                    val bottomSheetDialog =
                                        BottomSheetRetryFragment(
                                            txtTransactionamount.text.toString().replace(Regex("\\s+"), ""),
                                            isAcs,
                                            paymentModes,
                                            token
                                        )
                                    bottomSheetDialog.isCancelable = false
                                    bottomSheetDialog.show(supportFragmentManager, "")
                                    //mainViewModel.clearTransactionStatus()
                                } else {
                                    val intent = Intent(this, FailureActivity::class.java)
                                    //intent.putExtra(ORDER_ID, orderId)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        }
                    }, onFailure = {
                        val intent = Intent(this, FailureActivity::class.java)
                        //intent.putExtra(ORDER_ID, orderId)
                        startActivity(intent)
                        finish()
                        println("Transaction status failure")
                    })
                fetchDataResponseHandler.handleApiResult(response)

            }


        try {
            viewModel.fetch_response.observe(this) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<FetchResponse>(this@LandingActivity, onLoading = {
                        startShimmer()
                    }, onSuccess = { data ->
                        //orderId = response.order_id
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

            if (info.mobileNo != null) {
                findViewById<TextView>(R.id.txt_customer_id).visibility = View.VISIBLE
            }
            if (info.emailId != null) {
                findViewById<TextView>(R.id.txt_customer_email).visibility = View.VISIBLE
            }

            if (info.mobileNo != null && info.emailId != null) {
                findViewById<TextView>(R.id.seperator).visibility = View.VISIBLE
            }
        }

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
            fetchResponse?.merchantBrandingData?.palette?.let { palette ->
                this.palette = palette
                setColor()
                setStatusBarColor(this)
            }
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

        txtMerchantMobileNumber.text = fetchResponse?.customerInfo?.mobileNo
        txtMerchantEmailId.text = fetchResponse?.customerInfo?.emailId

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
        val color =
            if (palette != null) Color.parseColor(palette?.C900) else context.resources.getColor(R.color.header_color)
        getWindow().setStatusBarColor(color)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        println("Exception caught")
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        CleverTapUtil.CT_EVENT_SDK_ERROR(
            clevertapDefaultInstance,
            e.javaClass.toString(),
            e.printStackTrace().toString()
        )
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
        val retriever = client.startSmsRetriever()
        retriever.addOnSuccessListener {

        }
    }

    private fun getOtpFromMessage(message: String?) {
        println("OTP " + message)

        val otpPattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            /*etOTP.setText(matcher.group(0))*/
            println("OTP " + matcher.group(0))
            Toast.makeText(this@LandingActivity, "OTP " + matcher.group(0), Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    print("SMS Success ")
                    startActivityForResult(intent!!, REQ_USER_CONSENT)
                }

                override fun onFailure() {
                }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart();
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onRetry(isAcs: Boolean) {
        this.isAcs = isAcs
        retryViewModel.getTransactionStatus(token)
        //loadFragment(fragmentTag)
    }

}