package com.pinelabs.pluralsdk.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.AppSignatureHelper
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.OTPResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.data.utils.Utils.buttonBackground
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.NONE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_RESEND
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PROCESS_PAYMENT_REQUEST
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.SmsBroadcastReceiver
import com.pinelabs.pluralsdk.utils.SmsBroadcastReceiver.SmsBroadcastReceiverListener
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.RetryViewModel
import java.util.Timer
import java.util.regex.Matcher
import java.util.regex.Pattern

class OtpFragment : Fragment() {

    private lateinit var textResendOtp: TextView
    private lateinit var resendOtpTimer: LinearLayout
    private lateinit var textTimer: TextView
    private lateinit var linearAutoRead: LinearLayout
    private lateinit var btnVerifyContinue: Button
    private lateinit var linearAutoSubmit: LinearLayout
    private lateinit var linearAcsPage: LinearLayout
    private lateinit var constrainOTP: ConstraintLayout
    private lateinit var constrainSuccess: ConstraintLayout
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var objectAnimator: ObjectAnimator
    private lateinit var txtSubmitting: TextView
    private lateinit var txtStopAutoSubmit: TextView
    private lateinit var edtOtp: EditText
    private lateinit var imgStars: ImageView
    private lateinit var txtWaitAutoRead: TextView
    private lateinit var txtBankAcs: TextView
    private lateinit var imgBankAcs: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageButton
    private lateinit var token: String
    private lateinit var paymentId: String
    private lateinit var orderId: String
    private var resendEnable: Boolean? = false
    private var resendTimer: String? = "180"
    private var paymentRequest: ProcessPaymentRequest? = null
    private var palette: Palette? = null

    var t = Timer()
    private var countDownTimer: CountDownTimer? = null

    private var totalTime = 180000L
    private val interval = 1000L
    private var buttonClicked: Boolean = false

    val REQ_USER_CONSENT: Int = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private val retryViewModel by activityViewModels<RetryViewModel>()

    private var listener: onRetryListener? = null

    interface onRetryListener {
        fun onRetry(isAcs: Boolean, errorCode: String?, errorMessage: String?)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClicked = false

        val appSignatureHelper = AppSignatureHelper(requireActivity())
        Utils.println("Signature ${appSignatureHelper.appSignatures[0]}")

        startSmartUserConsent()

        token = arguments?.getString(TOKEN).toString()
        paymentId = arguments?.getString(PAYMENT_ID).toString()
        orderId = arguments?.getString(ORDER_ID).toString()
        resendEnable = arguments?.getBoolean(OTP_RESEND)
        resendTimer = /*arguments?.getString(RESEND_TIMER)*/"10"
        paymentRequest =
            arguments?.getParcelable<ProcessPaymentRequest>(PROCESS_PAYMENT_REQUEST)

        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.header_layout)
        activityButton.visibility = View.VISIBLE

        progressBar = view.findViewById<View>(R.id.progress_bar) as ProgressBar

        bottomSheetDialog = BottomSheetDialog(requireActivity())

        objectAnimator = ObjectAnimator.ofInt(
            progressBar, "progress",
            progressBar.progress, 100
        ).setDuration(2000)

        objectAnimator.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            progressBar.progress = progress
            if (progress == 100) {
                btnVerifyContinue.visibility = View.VISIBLE
                linearAutoSubmit.visibility = View.GONE
                val otpRequest = OTPRequest(paymentId, edtOtp.text.toString(), null, null, null)
                buttonClicked = true
                mainViewModel.submitOtp(token, otpRequest)
            }
        }

        txtSubmitting = view.findViewById<View>(R.id.text_view_button) as TextView
        txtSubmitting.setOnClickListener {
            //objectAnimator.start()
        }

        txtStopAutoSubmit = view.findViewById(R.id.txt_stop_auto_submit) as TextView
        txtStopAutoSubmit.setOnClickListener {
            btnVerifyContinue.visibility = View.VISIBLE
            linearAutoSubmit.visibility = View.GONE
            objectAnimator.cancel()
        }

        imgStars = view.findViewById(R.id.img_stars)
        txtWaitAutoRead = view.findViewById(R.id.txt_wait_auto_read)
        edtOtp = view.findViewById(R.id.et_otp)
        txtBankAcs = view.findViewById(R.id.txt_acs_website)
        imgBankAcs = view.findViewById(R.id.img_acs)
        textResendOtp = view.findViewById(R.id.resend_otp_text)
        if (resendEnable == true) textResendOtp.visibility =
            View.VISIBLE else textResendOtp.visibility = View.GONE
        resendOtpTimer = view.findViewById(R.id.linear_resend_otp_timer)
        textTimer = view.findViewById(R.id.resend_otp_timer)
        linearAutoRead = view.findViewById(R.id.linear_auto_read)
        btnVerifyContinue = view.findViewById(R.id.btnProceedToPay)
        btnVerifyContinue.background = buttonBackground(requireActivity(), palette)
        btnVerifyContinue.isEnabled = false
        btnVerifyContinue.alpha = 0.3f
        linearAcsPage = view.findViewById(R.id.linear_bank)
        linearAutoSubmit = view.findViewById(R.id.linearAutoSubmit)
        constrainOTP = view.findViewById(R.id.constrain_otp)
        constrainSuccess = view.findViewById(R.id.constrain_success)
        btnBack = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        edtOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtOtp.background = setColor(requireActivity())
                if (!imgStars.isVisible) linearAutoRead.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! >= 4) {
                    btnVerifyContinue.isEnabled = true
                    btnVerifyContinue.alpha = 1f
                } else {
                    btnVerifyContinue.isEnabled = false
                    btnVerifyContinue.alpha = 0.3f
                }
            }

        })

        mainViewModel.fetch_data_response.observe(viewLifecycleOwner) { response ->
            val fetchResponse =
                ApiResultHandler<FetchResponse>(
                    requireActivity(),
                    onLoading = {
                    },
                    onSuccess = { fetchResponse ->
                        btnVerifyContinue.background = buttonBackground(requireActivity(), palette)
                        fetchResponse?.merchantBrandingData?.palette?.let { palette ->
                            this.palette = palette
                            setPaletteColor(Color.parseColor(palette.C900))
                        }
                    },
                    onFailure = {})
            fetchResponse.handleApiResult(response)
        }

        mainViewModel.submit_otp_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                if (buttonClicked) {
                    val sendOtpResponseHandler =
                        ApiResultHandler<OTPResponse>(requireActivity(),
                            onLoading = {
                                showProcessPaymentDialog()
                            },
                            onSuccess = { data ->
                                bottomSheetDialog.cancel()
                                /*if (data?.status?.equals(OTP_SUCCESS) == true) {
                                    *//* val intent =
                                         Intent(requireActivity(), SuccessActivity::class.java)
                                     intent.putExtra(ORDER_ID, orderId)
                                     startActivity(intent)
                                     requireActivity().finish()*//*

                                }*/
                                retryViewModel.getTransactionStatus(token)
                            },
                            onFailure = { data ->
                                bottomSheetDialog.cancel()
                                if (data?.error_details?.error?.next?.contains(NONE) == true || data?.error_details == null) {
                                    listener?.onRetry(
                                        false,
                                        data?.error_code,
                                        data?.error_message
                                    )
                                } else {
                                    linearAutoRead.visibility = View.VISIBLE
                                    imgStars.visibility = View.GONE
                                    txtWaitAutoRead.text = getString(R.string.wrong_otp)
                                    txtWaitAutoRead.setTextColor(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            R.color.red
                                        )
                                    )
                                    edtOtp.background = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.edittext_error_border
                                    )
                                }
                            })
                    sendOtpResponseHandler.handleApiResult(response)
                }
            }

        mainViewModel.resend_otp_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val resendOtpResponseHandler =
                    ApiResultHandler<OTPResponse>(requireActivity(),
                        onLoading = {},
                        onSuccess = { data ->
                            resendEnable = data?.next?.contains(OTP_RESEND)
                        },
                        onFailure = { data ->
                            if (countDownTimer != null) {
                                countDownTimer!!.cancel()
                            }
                            resendOtpTimer.visibility = View.GONE
                            textResendOtp.visibility = View.GONE
                            imgStars.visibility = View.GONE
                            txtWaitAutoRead.text = data?.error_message
                            txtWaitAutoRead.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.red
                                )
                            )
                        })
                resendOtpResponseHandler.handleApiResult(response)
            }

        mainViewModel.process_payment_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                if (buttonClicked) {
                    val startTime = System.currentTimeMillis()
                    val responseHandler = ApiResultHandler<ProcessPaymentResponse>(
                        requireActivity(),
                        onLoading = {},
                        onSuccess = { data ->

                            //LandingActivity().paymentId = data?.payment_id
                            mainViewModel.paymentId.value = data?.payment_id

                            val arguments = Bundle()
                            arguments.putString(TOKEN, token)
                            arguments.putLong(START_TIME, startTime)
                            arguments.putString(
                                REDIRECT_URL,
                                data?.redirect_url
                            )
                            arguments.putString(ORDER_ID, orderId)
                            arguments.putString(
                                PAYMENT_ID,
                                paymentId
                            )
                            //arguments.putString(RETRY_PAGE, retryPage)

                            val acsFragment = ACSFragment()
                            acsFragment.arguments = arguments

                            requireActivity().supportFragmentManager.popBackStack()
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(
                                R.id.details_fragment,
                                acsFragment,
                                TAG_ACS
                            )
                            transaction.addToBackStack(TAG_ACS)
                            transaction.commit()
                        },
                        onFailure = { data ->
                            listener?.onRetry(
                                false,
                                data?.error_code,
                                data?.error_message
                            )
                        })
                    responseHandler.handleApiResult(response)
                }
            }

        linearAcsPage.setOnClickListener {
            buttonClicked = true
            mainViewModel.processPayment(token, paymentRequest)
        }

        textResendOtp.setOnClickListener {
            resendTimer(true)
            resendOtp(paymentId)
        }

        btnVerifyContinue.setOnClickListener {
            buttonClicked = true
            val otpRequest = OTPRequest(paymentId, edtOtp.text.toString(), null, null, null)
            mainViewModel.submitOtp(token, otpRequest)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQ_USER_CONSENT) {
            if ((resultCode === RESULT_OK) && (data != null)) {
                val message: String? = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
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

    fun resendTimer(visible: Boolean) {
        if (visible) {
            textResendOtp.visibility = View.GONE
            resendOtpTimer.visibility = View.VISIBLE
            linearAutoRead.visibility = View.VISIBLE
            startTimer()
        } else {
            textResendOtp.visibility = View.VISIBLE
            resendOtpTimer.visibility = View.GONE
            linearAutoRead.visibility = View.GONE
        }
    }

    private fun startTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        txtWaitAutoRead.text = getString(R.string.auto_read)
        if (palette != null) {
            txtWaitAutoRead.setTextColor(
                Color.parseColor(palette?.C900)
            )
            imgStars.imageTintList = ColorStateList.valueOf(
                Color.parseColor(palette?.C900)
            )
        } else {
            txtWaitAutoRead.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.header_color
                )
            )
        }

        resendTimer?.let { resendTimer -> totalTime = resendTimer.toLong() * 1000 }
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                textTimer.text = String.format(
                    "%02d:%02d",
                    secondsRemaining / 60,
                    secondsRemaining % 60
                )
            }

            override fun onFinish() {
                resendTimer(false)
                if (resendEnable == true) textResendOtp.visibility =
                    View.VISIBLE else textResendOtp.visibility = View.GONE
            }
        }.start()
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null)
        val retriever = client.startSmsRetriever()
        /*retriever.addOnSuccessListener { message ->

        }
        retriever.addOnFailureListener { message ->

        }*/
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPattern: Pattern = Pattern.compile("(|^)\\d{4,9}")
        val matcher: Matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            btnVerifyContinue.visibility = View.GONE
            linearAutoSubmit.visibility = View.VISIBLE
            txtSubmitting.setText(getString(R.string.submitting_otp))
            objectAnimator.start()
            edtOtp.setText(matcher.group(0))
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
        requireActivity().registerReceiver(
            smsBroadcastReceiver,
            intentFilter,
            Context.RECEIVER_EXPORTED
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart();
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }

    private fun resendOtp(paymentId: String) {
        val otpRequest = OTPRequest(paymentId, null, null, null, null)
        mainViewModel.resendOtp(token, otpRequest)
    }

    fun setPaletteColor(color: Int) {
        textResendOtp.setTextColor(color)
        txtBankAcs.setTextColor(color)
        imgBankAcs.imageTintList = ColorStateList.valueOf(
            Color.parseColor(palette?.C900)
        )
        txtStopAutoSubmit.setTextColor(color)
        progressBar.progressBackgroundTintList = ColorStateList.valueOf(
            Color.parseColor(palette?.C900)
        )
        btnVerifyContinue.background = buttonBackground(requireActivity(), palette)
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

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.process_payment_bottom_sheet, null)

        var logoAnimation: LottieAnimationView = view.findViewById(R.id.img_process_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}