package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.FailureActivity
import com.pinelabs.pluralsdk.data.model.UpiData
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.isNotNullAndBlank
import com.pinelabs.pluralsdk.PluralSDK
import com.pinelabs.pluralsdk.activity.LandingActivity
import com.pinelabs.pluralsdk.activity.SuccessActivity
import com.pinelabs.pluralsdk.adapter.DividerItemDecoratorHorizontal
import com.pinelabs.pluralsdk.adapter.UpiIntentAdapter
import com.pinelabs.pluralsdk.data.model.Extra
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentModeData
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.model.UpiTransactionData
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_CARDS
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_COLLECT
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_INTENT_ALL
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_INTENT_GPAY
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_INTENT_PAYTM
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_INTENT_PHONEPE
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_CODE
import com.pinelabs.pluralsdk.utils.Constants.Companion.GPAY
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_INITIATED
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYTM
import com.pinelabs.pluralsdk.utils.Constants.Companion.PHONEPE
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_COLLECT
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_INTENT
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_INTENT_PREFIX
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PAY_WITH
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_ATTEMPTED
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_FAILED
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_PROCESSED_STATUS
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_TRANSACTION_STATUS_DELAY
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_TRANSACTION_STATUS_INTERVAL
import com.pinelabs.pluralsdk.utils.PaymentModeId
import com.pinelabs.pluralsdk.utils.PaymentModes
import java.util.Timer
import java.util.TimerTask

class UPICollectFragment : Fragment(), UpiIntentAdapter.OnItemClickListener {

    private lateinit var etUPIId: EditText
    private lateinit var checkCircleIcon: ImageView
    private lateinit var btnVerifyContinue: Button
    private lateinit var btnBack: ImageButton
    private var amount: Int? = null
    private var currency: String? = null
    private var paymentOptionList = mutableListOf<String>()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private lateinit var token: String
    private lateinit var cancelPaymentTextView: TextView
    private var vpaContext: String? = null
    private lateinit var recyclerPBPApps: RecyclerView
    private lateinit var constrainIntent: ConstraintLayout
    private lateinit var divider: LinearLayout
    private lateinit var linearCollect: LinearLayout
    private lateinit var linearCollectBorder: LinearLayout

    private lateinit var circularProgressBar: ProgressBar
    private lateinit var timerTextView: TextView
    private lateinit var btnPayByUpiApp: Button
    private var palette: Palette? = null
    private val totalTime = 30000L
    private val interval = 1000L

    internal val UPI_PAYMENT = 0

    private val UPI_REGEX = Regex("^[\\w.]{1,}-?[\\w.]{0,}-?[\\w.]{1,}@[a-zA-Z]{2,}$")
    val upiList = mutableListOf<Int>()

    var t = Timer()
    private var countDownTimer: CountDownTimer? = null

    private var clevertapDefaultInstance: CleverTapAPI? = null
    private var listener: onRetryListener? = null
    private var buttonClicked: Boolean = false

    var orderId: String? = null
    var paymentId: String? = null

    var transactionMode: String? = null
    var upiAppPackageName: String? = null

    interface onRetryListener {
        fun onRetry(isAcs: Boolean, errorMessage:String?)
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating the layout
        return inflater.inflate(R.layout.upicollect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        buttonClicked = false
        (activity as LandingActivity).deepLink = null

        // Initializing Views
        etUPIId = view.findViewById(R.id.etUPIId)
        checkCircleIcon = view.findViewById(R.id.check_circle_icon)
        btnVerifyContinue = view.findViewById(R.id.btnVerifyContinue)
        btnPayByUpiApp = view.findViewById(R.id.btn_proceed_pay)
        btnBack = view.findViewById(R.id.upiBtnBack)
        recyclerPBPApps = view.findViewById(R.id.recycler_upi_apps)

        constrainIntent = view.findViewById(R.id.constrain_upi_intent)
        divider = view.findViewById(R.id.linear_divider)
        linearCollect = view.findViewById(R.id.linear_upi_collect)
        linearCollectBorder = view.findViewById(R.id.upi_collect_border)

        bottomSheetDialog = BottomSheetDialog(requireActivity())

        // Retrieve token from arguments
        token = arguments?.getString(TOKEN).toString()

        getUpiAppsInstalledInDevice()
        if (upiList.size == 0) {
            recyclerPBPApps.visibility = View.GONE
        } else {
            recyclerPBPApps.layoutManager = GridLayoutManager(requireActivity(), upiList.size)

            val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecoratorHorizontal(
                ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!
            )
            recyclerPBPApps.addItemDecoration(dividerItemDecoration)
            val adapter = UpiIntentAdapter(upiList, this)
            recyclerPBPApps.adapter = adapter
        }


        btnVerifyContinue.setOnClickListener {
            buttonClicked = true
            val vpa = etUPIId.text.toString()
            payAction(vpa, UPI_COLLECT, null)
        }
        btnVerifyContinue.background = buttonBackground(requireActivity())
        btnVerifyContinue.isEnabled = false
        btnVerifyContinue.alpha = 0.3f

        btnPayByUpiApp.setOnClickListener {
            buttonClicked = true
            payAction(null, UPI_INTENT, null)
        }

        // Set up back button click listener
        btnBack.setOnClickListener {
            //clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, false, true) }
            //requireActivity().supportFragmentManager.popBackStack()
            requireActivity().onBackPressed()
        }

        mainViewModel.transaction_status_response.observe(viewLifecycleOwner) { response ->
            if (buttonClicked) {
                val transactionStatusResponseHandler =
                    ApiResultHandler<TransactionStatusResponse>(requireActivity(),
                        onLoading = {
                        }, onSuccess = { data ->
                            if (buttonClicked) {
                                if (response?.data!!.data.status.equals(UPI_PROCESSED_STATUS)) {
                                    bottomSheetDialog.dismiss()

                                    CleverTapUtil.CT_EVENT_PAYMENT_STATUS_SUCCESS(
                                        clevertapDefaultInstance, orderId, paymentId
                                    )
                                    val intent =
                                        Intent(requireActivity(), SuccessActivity::class.java)
                                    intent.putExtra(ORDER_ID, orderId)
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else if (response.data!!.data.status.equals(UPI_PROCESSED_FAILED) || response.data!!.data.status.equals(
                                        UPI_PROCESSED_ATTEMPTED
                                    )
                                ) {
                                    buttonClicked = false
                                    bottomSheetDialog.dismiss()
                                    listener?.onRetry(false, "")
                                    /*val intent = Intent(requireActivity(), FailureActivity::class.java)
                                    intent.putExtra(ERROR_MESSAGE, UPI_PROCESSED_FAILED)
                                    startActivity(intent)
                                    requireActivity().finish()*/
                                }
                                println("Transaction status ${response.data!!.data.status}")
                            }
                        }, onFailure = { errorMessage ->
                            /*bottomSheetDialog.dismiss()
                            listener?.onRetry(false)*/
                            val intent = Intent(requireActivity(), FailureActivity::class.java)
                            intent.putExtra(ERROR_CODE, errorMessage?.error_code)
                            intent.putExtra(ERROR_MESSAGE, errorMessage?.error_message)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    )
                transactionStatusResponseHandler.handleApiResult(response)
            }

        }

        mainViewModel.process_payment_response.observe(viewLifecycleOwner) { response ->
            if (buttonClicked) {
                val fetchDataResponseHandler =
                    ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                        onLoading = {
                            showProcessPaymentDialog()
                        }, onSuccess = { response ->
                            LandingActivity().paymentId = response?.payment_id
                            if (response!!.deep_link != null && transactionMode == UPI_INTENT) {
                                (activity as LandingActivity).deepLink =
                                    response!!.deep_link.toString()
                                showUpiTray(response!!.deep_link.toString(), upiAppPackageName)
                            }
                            orderId = response.order_id
                            paymentId = response.payment_id
                            getTransactionStatus(token)
                        }, onFailure = { errorMessage ->
                            bottomSheetDialog.dismiss()
                            /*val intent = Intent(requireActivity(), FailureActivity::class.java)
                            intent.putExtra(ERROR_CODE, errorMessage?.error_code)
                            intent.putExtra(ERROR_MESSAGE, errorMessage?.error_message)
                            startActivity(intent)
                            requireActivity().finish()*/
                            listener?.onRetry(false, errorMessage?.error_message)
                        })
                fetchDataResponseHandler.handleApiResult(response)
            }
        }

        setupUPIIdValidation()
        fetchDataListener()
    }

    private fun setupUPIIdValidation() {
        etUPIId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isValidUPI = UPI_REGEX.matches(s.toString())

                // Show/hide check_circle icon based on validation
                checkCircleIcon.visibility = if (isValidUPI) View.VISIBLE else View.GONE

                // Enable/disable button and change color based on validation
                btnVerifyContinue.background = buttonBackground(requireActivity())

                btnVerifyContinue.isEnabled = isValidUPI
                if (btnVerifyContinue.isEnabled) {
                    btnVerifyContinue.alpha = 1F
                } else {
                    btnVerifyContinue.alpha = 0.3F
                }
                //val color = if (isValidUPI) R.color.colorSecondary else R.color.colorPrimary
            }
        })
        etUPIId.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                linearCollectBorder.background = setColor(requireContext())
            } else {
                linearCollectBorder.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.edittext_default_border
                )!!
            }
        }
    }

    fun setColor(context: Context): Drawable {
        val drawable: Drawable
        if (palette != null) {
            drawable =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.edittext_border_focussed
                ) as GradientDrawable
            drawable.setStroke(convertDpToPx(2), Color.parseColor(palette?.C900))
        } else {
            drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.edittext_border_focussed
            )!!
        }
        return drawable
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun payAction(
        vpa: String?,
        transactionMode: String?,
        upiAppPackageName: String?
    ) {
        val upiMethod = if (vpa.isNotNullAndBlank()) CT_COLLECT else
            if (upiAppPackageName.equals(GPAY)) CT_INTENT_GPAY else
                if (upiAppPackageName.equals(PHONEPE)) CT_INTENT_PHONEPE else
                    if (upiAppPackageName.equals(PAYTM)) CT_INTENT_PAYTM else CT_INTENT_ALL
        CleverTapUtil.CT_EVENT_PAYMENT_METHOD(
            clevertapDefaultInstance, CT_UPI, PAYMENT_INITIATED,
            null, upiMethod, null
        )
        val paymentMode = arrayListOf(UPI_ID)
        val cardDataExtra = Extra(paymentMode, amount, currency, null, null, null, null, null)
        val upiTxnMode = transactionMode
        val upiData = UpiData(UPI, vpa, upiTxnMode)
        val upiTxnData = UpiTransactionData(PaymentModeId.UPI.id)
        val processPaymentRequest =
            ProcessPaymentRequest(card_data = null, upiData, null, cardDataExtra, upiTxnData, null)
        vpaContext = vpa

        this.transactionMode = transactionMode
        this.upiAppPackageName = upiAppPackageName

        if ((transactionMode.equals(UPI_INTENT) && (activity as LandingActivity).deepLink == null) || transactionMode.equals(
                UPI_COLLECT
            )
        ) {
            mainViewModel.processPayment(token, processPaymentRequest)
        } else {
            showProcessPaymentDialog()
            if ((activity as LandingActivity).deepLink != null && transactionMode == UPI_INTENT)
                showUpiTray((activity as LandingActivity).deepLink.toString(), upiAppPackageName)
            getTransactionStatus(token)
        }
    }


    private fun fetchDataListener() {
        mainViewModel.fetch_response.observe(viewLifecycleOwner) { response ->
            val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                onLoading = {
                }, onSuccess = { data ->
                    palette = data?.merchantBrandingData?.palette
                    val paymentModeData = data!!.paymentModes!!.filter { paymentMode ->
                        paymentMode.paymentModeId.equals(
                            UPI
                        )
                    }.filter { paymentMode -> paymentMode.paymentModeData != null }.toList()
                    if (paymentModeData.size > 0) {
                        val pm = paymentModeData.get(0).paymentModeData as? PaymentModeData
                        pm?.upi_flows?.forEach { upiOption ->
                            paymentOptionList.add(upiOption)
                            println(upiOption)
                        }
                        makeViewVisible(paymentOptionList)
                    }
                    amount = data!!.paymentData!!.originalTxnAmount?.amount
                    currency = data.paymentData!!.originalTxnAmount?.currency
                }, onFailure = {
                }
            )
            fetchDataResponseHandler.handleApiResult(response)
        }
    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.activity_timer, null)
        cancelPaymentTextView = view.findViewById(R.id.cancelPaymentTextView)
        var vpaId: TextView = view.findViewById(R.id.upiIdTextView)
        vpaId.text = vpaContext

        cancelPaymentTextView.setOnClickListener {
            bottomSheetDialog.dismiss()
            requireActivity().onBackPressed()
        }

        circularProgressBar = view.findViewById(R.id.circularProgressBar)
        if (palette != null) {
            circularProgressBar.progressTintList =
                ColorStateList.valueOf(Color.parseColor(palette?.C900))
            cancelPaymentTextView.setTextColor(Color.parseColor(palette?.C900))
        }
        timerTextView = view.findViewById(R.id.timerTextView)

        startTimer()
        // Initialize BottomSheetDialog
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show() // Show the dialog first
    }

    private fun startTimer() {
        circularProgressBar.progress = 100
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = String.format(
                    "%02d:%02d",
                    secondsRemaining / 60,
                    secondsRemaining % 60
                )
                val progressPercentage = (millisUntilFinished * 100 / totalTime).toInt()
                circularProgressBar.progress = progressPercentage
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                circularProgressBar.progress = 0

                bottomSheetDialog.dismiss()
                listener?.onRetry(false, "")
                /*requireActivity().finish()
                PluralSDK.getInstance().callback!!.onSuccessOccured("")*/
            }
        }.start()
    }

    private fun showCancelConfirmationDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.cancel_confirmation_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)
        val btnYes: Button = view.findViewById(R.id.btn_yes)
        val btnNo: Button = view.findViewById(R.id.btn_no)

        btnYes.setOnClickListener {
            bottomSheetDialog.dismiss()
            requireActivity().onBackPressed()
            //mainViewModel.cancelTransaction(token)
            /*Toast.makeText(requireActivity(), "Transaction cancelled", Toast.LENGTH_SHORT).show()
            requireActivity().finish()*/
        }

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getUpiAppsInstalledInDevice(): List<Int> {
        if (isAppInstalled(GPAY) && isAppUpiReady(GPAY)) upiList.add(R.drawable.google_pay)
        if (isAppInstalled(PHONEPE) && isAppUpiReady(PHONEPE)) upiList.add(R.drawable.phone_pe)
        if (isAppInstalled(PAYTM) && isAppUpiReady(PAYTM)) upiList.add(R.drawable.paytm)
        return upiList.toList()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = requireActivity().packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return pm.getApplicationInfo(packageName, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
    }

    private fun showUpiTray(deepLink: String, upiAppPackageName: String?) {
        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = Uri.parse(deepLink)
        if (upiAppPackageName != null) {
            upiPayIntent.`package` = upiAppPackageName
            //isAppUpiReady(upiAppPackageName)
        }
        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, UPI_PAY_WITH)
        // check if intent resolves
        if (null != chooser.resolveActivity(requireActivity().packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(
                requireActivity(),
                "No UPI app found, please install one to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onItemClick(position: Int) {
        buttonClicked = true
        when (position) {
            0 -> payAction(null, UPI_INTENT, GPAY)
            1 -> payAction(null, UPI_INTENT, PHONEPE)
            2 -> payAction(null, UPI_INTENT, PAYTM)
        }
    }

    fun isAppUpiReady(packageName: String): Boolean {
        var appUpiReady = false
        val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse(UPI_INTENT_PREFIX))
        val pm = requireActivity().packageManager
        val upiActivities: List<ResolveInfo> = pm.queryIntentActivities(upiIntent, 0)
        for (a in upiActivities) {
            if (a.activityInfo.packageName == packageName) appUpiReady = true
        }
        return appUpiReady
    }

    private fun getTransactionStatus(token: String) {
        t.schedule(
            object : TimerTask() {
                override fun run() {
                    mainViewModel.getTransactionStatus(token)
                }
            },
            UPI_TRANSACTION_STATUS_INTERVAL,
            UPI_TRANSACTION_STATUS_INTERVAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        t.cancel()
    }

    private fun makeViewVisible(upiOptions: List<String>) {
        upiOptions.forEach { upiOption ->
            if (upiOption.equals(UPI_INTENT)) {
                constrainIntent.visibility = View.VISIBLE
            }
            if (upiOption.equals(UPI_COLLECT)) {
                linearCollect.visibility = View.VISIBLE
                btnVerifyContinue.visibility = View.VISIBLE
            }
        }
        if (upiOptions.size == 2) divider.visibility = View.VISIBLE
    }

    public fun buttonBackground(context: Context): Drawable {

        val stateListDrawable = StateListDrawable()

        // Create different drawables for different states
        val pressedDrawable = GradientDrawable().apply {
            if (palette != null) {
                setColor(Color.parseColor(palette?.C900))
            } else {
                setColor(context.resources.getColor(R.color.header_color))
            }
            cornerRadius = 16f // Normal corner radius
        }

        // Add states to the StateListDrawable
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), pressedDrawable) // Default state

        return stateListDrawable
    }

}