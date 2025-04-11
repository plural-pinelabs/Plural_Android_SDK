package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.UpdateOrderDetails
import com.pinelabs.pluralsdk.data.utils.Utils.buttonBackground
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_DETAILS
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_ATTEMPT
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPACE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_BOTTOM_SHEET_MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.SavedCardViewModel

class BottomSheetOtp(palette: Palette?) : BottomSheetDialogFragment() {

    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private val savedCardViewModel by activityViewModels<SavedCardViewModel>()

    private lateinit var edtOTP: EditText
    private lateinit var txtOtpDescription: TextView
    private lateinit var imgClose: ImageView
    private lateinit var txtFooterText: TextView
    private lateinit var txtResendTimer: TextView
    private lateinit var txtResendOtp: TextView
    private lateinit var txtOtpError: TextView
    private lateinit var imgIcon: ImageView
    private lateinit var linearResendTimer: LinearLayout
    private lateinit var linearResendOtp: LinearLayout

    private var token: String? = null
    private var otpId: String? = null
    private var customerId: String? = null
    private var mobileNumber: String? = null
    private var customerInfo: CustomerInfo? = null

    private var countDownTimer: CountDownTimer? = null
    private var resendTimer: String? = "60"

    private var totalTime = 180000L
    private val interval = 1000L

    private var palette: Palette? = palette

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_card_mobile_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mobileNumber = arguments?.getString(MOBILE)
        token = arguments?.getString(TOKEN)
        otpId = arguments?.getString(OTP_ID)
        customerId = arguments?.getString(CUSTOMER_ID)
        customerInfo = arguments?.getParcelable(CUSTOMER_DETAILS)

        mainViewModel.otpId.observe(viewLifecycleOwner) { otpId ->
            this.otpId = otpId
        }

        savedCardViewModel.otpId.observe(viewLifecycleOwner) { otpId ->
            this.otpId = otpId
        }

        imgClose = view.findViewById(R.id.img_close)
        imgClose.setOnClickListener {
            this.dismiss()

            val bottomSheet =
                requireActivity().supportFragmentManager.findFragmentByTag(TAG_BOTTOM_SHEET_MOBILE)
            if (bottomSheet != null && bottomSheet is BottomSheetDialogFragment) {
                bottomSheet.dismiss()
            }

        }

        txtOtpError = view.findViewById(R.id.txt_mobile_error)
        txtFooterText = view.findViewById(R.id.txt_footer_text)

        imgIcon = view.findViewById(R.id.img_icon)
        if (palette != null) {
            val layerDrawable = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.save_card_otp_drawable_layer
            ) as LayerDrawable
            val gradientDrawable =
                layerDrawable.findDrawableByLayerId(R.id.icon_bg) as VectorDrawable
            gradientDrawable.setTint(Color.parseColor(palette?.C900))
            imgIcon.setImageDrawable(layerDrawable)
        }

        val buttonClick = view.findViewById<Button>(R.id.btnProceedToPay)
        buttonClick.background = buttonBackground(requireActivity(), palette)
        buttonClick.isEnabled = false
        buttonClick.alpha = 0.3F

        edtOTP = view.findViewById(R.id.edt_mobile_number)
        edtOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtOtpError.visibility = View.GONE
                if (s?.length == 6) {
                    buttonClick.isEnabled = true
                    buttonClick.alpha = 1F
                } else {
                    buttonClick.isEnabled = false
                    buttonClick.alpha = 0.3F
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        txtOtpDescription = view.findViewById(R.id.txt_otp_content)
        if (customerInfo == null) {
            txtOtpDescription.text =
                txtOtpDescription.text.toString() + SPACE + mobileNumber + SPACE + context?.getString(
                    R.string.securely_save_card_otp
                )
            txtFooterText.text = getString(R.string.skip_saving_card)
        } else {
            txtOtpDescription.text =
                txtOtpDescription.text.toString() + SPACE + mobileNumber + SPACE + context?.getString(
                    R.string.to_proceed
                )
            txtFooterText.text = getString(R.string.edit_mobile_number)
        }

        txtResendTimer = view.findViewById(R.id.resend_otp_timer)
        txtResendOtp = view.findViewById(R.id.resend_otp)

        linearResendTimer = view.findViewById(R.id.linear_resend_otp_timer)
        linearResendOtp = view.findViewById(R.id.linear_resend_otp)
        startTimer()

        txtResendOtp.setOnClickListener {
            if (OTP_ATTEMPT > 0) {
                val otpRequest = OTPRequest(null, null, customerId, null, null)
                if (customerInfo != null)
                    mainViewModel.sendOTPCustomer(token, otpRequest)
                else
                    savedCardViewModel.sendOTPCustomer(token, otpRequest)
                OTP_ATTEMPT--
                startTimer()
            } else {
                txtOtpError.visibility = View.VISIBLE
                txtOtpError.text = getString(R.string.otp_attempt_exceeded)
            }
        }

        buttonClick.setOnClickListener {
            val otp = edtOTP.text.toString()
            if (otp.isEmpty()) {
                txtOtpError.visibility = View.VISIBLE
                txtOtpError.text = getString(R.string.enter_otp)
            } else {
                if (customerInfo != null) {
                    customerInfo?.email_id = customerInfo?.emailId
                    customerInfo?.mobile_number = customerInfo?.mobileNumber
                    customerInfo?.country_code = customerInfo?.countryCode
                    customerInfo?.is_edit_customer_details_allowed =
                        customerInfo?.isEditCustomerDetailsAllowed
                    customerInfo?.first_name = customerInfo?.firstName
                    customerInfo?.last_name = customerInfo?.lastName

                    val updateOrderDetails = UpdateOrderDetails(customerInfo)
                    val otpRequest = OTPRequest(null, otp, customerId, otpId, updateOrderDetails)
                    mainViewModel.validateUpdateOrder(token, otpRequest)
                } else {
                    val otpRequest = OTPRequest(null, otp, customerId, otpId, null)
                    savedCardViewModel.validateOTPCustomer(token, otpRequest)
                }
            }
        }

        txtFooterText.setOnClickListener {
            if (customerInfo == null) {
                savedCardViewModel.skipSavedCard.value = true
            }
            this@BottomSheetOtp.dismiss()

            /*if (customerInfo != null) {
                val arguments = Bundle()
                arguments.putSerializable(CUSTOMER_DETAILS, customerInfo)
                arguments.putString(TOKEN, token)
                arguments.putString(MOBILE, customerInfo?.mobileNumber)
                arguments.putString(EMAIL, customerInfo?.emailId)

                val bottomSheetDialog = BottomSheetMobileNumber()
                bottomSheetDialog.arguments = arguments

                bottomSheetDialog.isCancelable = false
                bottomSheetDialog.show(requireActivity().supportFragmentManager, "")
            }*/

        }

        mainViewModel.savedCardOtpError.observe(this) { response ->
            txtOtpError.visibility = View.VISIBLE
            txtOtpError.text = getString(R.string.wrong_otp)
        }
    }

    private fun startTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        linearResendOtp.visibility = View.GONE
        linearResendTimer.visibility = View.VISIBLE

        resendTimer?.let { resendTimer -> totalTime = resendTimer.toLong() * 1000 }
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                txtResendTimer.text = String.format(
                    "%02d:%02d",
                    secondsRemaining / 60,
                    secondsRemaining % 60
                )
            }

            override fun onFinish() {
                linearResendOtp.visibility = View.VISIBLE
                linearResendTimer.visibility = View.GONE
            }
        }.start()
    }

}