package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.FlexAdapter
import com.pinelabs.pluralsdk.adapter.PBPBanksAdapter
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequest
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequestList
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponse
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponseData
import com.pinelabs.pluralsdk.data.model.CardData
import com.pinelabs.pluralsdk.data.model.DccData
import com.pinelabs.pluralsdk.data.model.DeviceInfo
import com.pinelabs.pluralsdk.data.model.Extra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.OTPResponse
import com.pinelabs.pluralsdk.data.model.OrderDetails
import com.pinelabs.pluralsdk.data.model.OrderDetailsAmount
import com.pinelabs.pluralsdk.data.model.PBPBank
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RewardPaymentOption
import com.pinelabs.pluralsdk.data.model.RewardPointsCardDetails
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.model.RewardResponse
import com.pinelabs.pluralsdk.data.model.SavedCardResponse
import com.pinelabs.pluralsdk.utils.AmountUtil.convertToRupees
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.ColumnUtil
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.data.utils.Utils.buttonBackground
import com.pinelabs.pluralsdk.data.utils.Utils.cleverTapLog
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.ALLAHABAD
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.ANDHRA
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.AU
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.CANARA_BANK
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.CENTRAL
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.CORPORATION
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.FEDERAL
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.IDBI
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.IDFC
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.INDIAN
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.KARUR
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.PNB
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.SBI
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.SOUTH_INDIAN_BANK
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.UNION_BANK
import com.pinelabs.pluralsdk.utils.BankConstant.Companion.YES
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.AMOUNT
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_ALLAHABAD
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_ANDHRA
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_AU_SMALL
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_CANARA
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_CENTRAL
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_CORPORATION
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_FEDERAL
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_IDBI
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_IDFC
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_IDIAN
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_KARUR_VYSYA
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_PNB
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_PUNJAB
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_SBI
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_SOUTH_INDIAN
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_STATE_BANK
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_UNION
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_YES
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_YES_BANK
import com.pinelabs.pluralsdk.utils.Constants.Companion.BIN_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_ACCEPT_ALL
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_USER_AGENT_ANDROID
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_CARDS
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.DCC_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.OTP_RESEND
import com.pinelabs.pluralsdk.utils.Constants.Companion.PALETTE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_INITIATED
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_REFERENCE_TYPE_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_ANOTHER_CARD_NUMBER
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_ANOTHER_MOBILE_NUMBER
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_BANK_VISIBLE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_CANNOT_CHECK_POINTS
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_CHECK_POINTS
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_LOADING
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_POINTS
import com.pinelabs.pluralsdk.utils.Constants.Companion.PBP_ZERO_POINTS
import com.pinelabs.pluralsdk.utils.Constants.Companion.PROCESS_PAYMENT_REQUEST
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.RESEND_TIMER
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_OTP
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.DCC_STATUS
import com.pinelabs.pluralsdk.utils.DeviceUtil
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.SavedCardViewModel
import java.util.Calendar
import java.util.Locale


class CardFragment : Fragment() {

    private val cardTypes = mapOf(
        "AMEX" to "^3[47]\\d{13}$".toRegex(),
        "BCGlobal" to "^(6541|6556)\\d{12}$".toRegex(),
        "Carte Blanche" to "^389\\d{11}$".toRegex(),
        "Diners Club" to "^3(?:0[0-5]|[68]\\d)\\d{11}$".toRegex(),
        "Discover" to "^(6[54][4-9]\\d{12}|622(12[6-9]|1[3-9]\\d|[2-8]\\d\\d|9[01]\\d|92[0-5])\\d{10})$".toRegex(),
        "Insta Payment" to "^63[7-9]\\d{13}$".toRegex(),
        "JCB" to "^(?:2131|1800|35\\d{3})\\d{11}$".toRegex(),
        "KoreanLocal" to "^9\\d{15}$".toRegex(),
        "Laser" to "^(6304|6706|6709|6771)\\d{12,15}$".toRegex(),
        "Maestro" to "^(5018|5020|5038|6304|6759|6761|6763)\\d{8,15}$".toRegex(),
        "MASTERCARD" to "^(5[1-5]\\d{14}|2(22[1-9]|2[3-9]\\d|[3-6]\\d\\d|7[01])\\d{12})$".toRegex(),
        "RUPAY" to Regex("^6(?!011)(?:\\d{15}|52[12]\\d{12})$"),
        "Solo" to "^((6334|6767)\\d{12}|(6334|6767)\\d{14}|(6334|6767)\\d{15})$".toRegex(),
        "Switch" to "^((49(0[35]|1[16]|36)|6(333|759))\\d{12,15}|564182\\d{10,13}|633110\\d{10,13})$".toRegex(),
        "Union Pay" to "^(62\\d{14,17})$".toRegex(),
        "VISA" to "^4\\d*$".toRegex(),
        "Visa Master" to "^(?:4\\d{12}(?:\\d{3})?|5[1-5]\\d{14})$".toRegex()
    )

    // Mapping of card types to brand icons
    private val cardIcons = mapOf(
        "AMEX" to R.drawable.amex,
        "VISA" to R.drawable.visa,
        "MASTERCARD" to R.drawable.mc,
        "RUPAY" to R.drawable.rupay,
        "Diners Club" to R.drawable.diners
    )
    private lateinit var token: String
    private var cardLast4: String? = null
    private var redeemableAmount: Int? = null
    private var mobileNumber: String? = null
    private var amount: Int? = null
    private var pbpCardNumber: String? = null
    private var currency: String? = null
    private var dccList: DccData? = null
    private var binData: CardBinMetaDataResponseData? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetSavedCardInfo: BottomSheetDialogFragment? = null
    private lateinit var bottomSheetOtp: BottomSheetDialogFragment
    private var bottomSheetDccDialog: BottomSheetDialogFragment? = null
    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private val savedCardViewModel by activityViewModels<SavedCardViewModel>()

    private var isCardNumberValid = false
    private var isExpiryValid = false
    private var isCVVValid = false
    private var isCardHolderNameValid = false
    private var isPBPChecked = false
    private var isPBPEnabled = false
    private var isSavedCardChecked = false
    private var isSavedCardEnabled = false
    private var isDCCEnabled = false
    private var isDCCEligible = false
    private var isNativeOTP: Boolean? = false
    private var isSaveCard: Boolean? = false

    private var orderId: String? = null
    private var customerId: String? = null
    private var paymentId: String? = null
    private var palette: Palette? = null
    private var startTime: Long? = null
    private var redirectUrl: String? = null
    private var skipSavedCard: Boolean = false
    private var buttonText: String? = null

    private lateinit var etCardNumber: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etCardHolderName: EditText
    private lateinit var etCVV: EditText
    private lateinit var btnProceedToPay: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var constraintLayoutPBPBanner: ConstraintLayout
    private lateinit var constraintLayoutCannotCheck: ConstraintLayout
    private lateinit var constraintLayoutAnotherNumber: ConstraintLayout
    private lateinit var constraintLayoutAnotherCard: ConstraintLayout
    private lateinit var constraintLayoutNoBalance: ConstraintLayout
    private lateinit var constraintLayoutRedeemPoints: ConstraintLayout
    private lateinit var constraintLayoutCheckingPoints: ConstraintLayout
    private lateinit var constraintLayoutSavedCard: ConstraintLayout
    private lateinit var checkBoxSavedCard: CheckBox
    private lateinit var textRbiGuideline: TextView
    private lateinit var linearCheckPoint: LinearLayout
    private lateinit var textCheckPoints: TextView
    private lateinit var textTryAnotherNumber: TextView

    private lateinit var spannableString: SpannableString
    private lateinit var cleanedInput: String
    private lateinit var processPaymentRequest: ProcessPaymentRequest

    private var clevertapDefaultInstance: CleverTapAPI? = null

    private var listener: onRetryListener? = null
    private var buttonClicked: Boolean = false
    private var retryTimer: Float? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cardlayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //println("SDK Details ${Gson().toJson(createSDKData())}")

        bottomSheetDialog = BottomSheetDialog(requireActivity())
        isSavedCardEnabled = false
        buttonClicked = false
        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.header_layout)
        activityButton.visibility = View.VISIBLE

        Utils.println("Require activity name ${requireActivity().packageName}")
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        cleverTapLog()

        token = arguments?.getString(TOKEN).toString()

        getView(view)

        spannableString = SpannableString(resources.getString(R.string.no_points))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                showPBPMobileNumberDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setUnderlineText(false)    // this remove the underline
            }
        }
        val startPosition: Int =
            textTryAnotherNumber.text.toString().indexOf(resources.getString(R.string.try_another))
        val endPosition: Int = textTryAnotherNumber.text.toString()
            .lastIndexOf(resources.getString(R.string.try_another)) + resources.getString(R.string.try_another).length
        spannableString.setSpan(
            clickableSpan, startPosition, endPosition,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        textTryAnotherNumber.setText(spannableString)
        textTryAnotherNumber.setMovementMethod(LinkMovementMethod.getInstance());

        textCheckPoints.setOnClickListener {
            showPBPMobileNumberDialog()
        }
        //pbpBankList()

        etCardNumber = view.findViewById(R.id.etCardNumber)
        etExpiry = view.findViewById(R.id.etExpiry)
        val tvExpiryError: TextView = view.findViewById(R.id.tvExpiryError)
        etCardHolderName = view.findViewById(R.id.etCardHolderName)
        etCVV = view.findViewById(R.id.etCVV)
        val tvCVVError: TextView = view.findViewById(R.id.tvCVVError)
        btnProceedToPay = view.findViewById(R.id.btnProceedToPay)
        val tvCardNumberError: TextView = view.findViewById(R.id.tvCardNumberError)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnProceedToPay.isEnabled = false
        btnProceedToPay.alpha = 0.3f

        //btnProceedToPay.setBackgroundColor(resources.getColor(R.color.shimmer_grey))

        // Set up card number validation
        setupCardNumberValidation(etCardNumber, tvCardNumberError)

        // Set up expiry date validation
        setupExpiryValidation(etExpiry, tvExpiryError, etCVV)

        // Set up CVV validation
        setupCVVValidation(etCVV, tvCVVError)

        // Set up cardholder name validation
        setupCardHolderNameValidation(etCardHolderName)

        btnProceedToPay.setOnClickListener {

            buttonClicked = true
            // Handle payment process here

            val processPaymentRequest = createProcessPaymentRequest()

            if (isDCCEligible) {
                showDCCDialog(binData, dccList)
                //showAddressPage(processPaymentRequest)
            } else if (isSavedCardChecked) {
                val otpRequest = OTPRequest(null, null, customerId, null, null)
                savedCardViewModel.sendOTPCustomer(token, otpRequest)
            } else if (isSavedCardEnabled && !skipSavedCard) {
                val argument = Bundle()
                argument.putString(
                    CUSTOMER_ID,
                    customerId
                )
                argument.putString(TOKEN, token)
                argument.putString(
                    MOBILE,
                    mobileNumber
                )
                argument.putParcelable(
                    PROCESS_PAYMENT_REQUEST,
                    processPaymentRequest
                )
                showSavedCardDialog(argument)
            } else {
                mainViewModel.processPayment(token, processPaymentRequest)
            }
            //showMoreBankDialog()


        }

        btnBack.setOnClickListener {
            //clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, false, true) }
            //requireActivity().supportFragmentManager.popBackStack()
            requireActivity().onBackPressed()
        }

        etCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cardNumber = etCardNumber.text.toString().replace(" ", "")
                if (cardNumber.length < 12) {
                    tvCardNumberError.visibility = View.VISIBLE
                    tvCardNumberError.text = "Your card number is incomplete"
                    etCardNumber.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_error_border
                    )
                    isCardNumberValid = false
                } else {
                    val cardType = validateCardType(cardNumber)
                    if (cardType.isNullOrEmpty()) {
                        tvCardNumberError.visibility = View.VISIBLE
                        tvCardNumberError.text = "Invalid card type"
                        etCardNumber.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.edittext_error_border
                        )
                        isCardNumberValid = false
                    } else {
                        if (validCard(cardNumber)) {
                            tvCardNumberError.visibility = View.INVISIBLE
                            etCardNumber.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.edittext_default_border
                            )
                            isCardNumberValid = true
                            // Set the brand icon based on the card type
                            //setCardBrandIcon(etCardNumber, cardType)
                        } else {
                            tvCardNumberError.visibility = View.VISIBLE
                            tvCardNumberError.text = "Invalid card number"
                            etCardNumber.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.edittext_error_border
                            )
                            isCardNumberValid = false
                        }
                    }
                }
                updateButtonBackground() // Update the button background based on validation
            }
        }
        //fetchDataListener()
        allViewsInvisible()
        observeListener()
    }

    fun getView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_banks)
        constraintLayoutPBPBanner = view.findViewById(R.id.constrain_pbp)
        constraintLayoutCannotCheck = view.findViewById(R.id.constrain_unable_to_check)
        constraintLayoutNoBalance = view.findViewById(R.id.constrain_no_balance)
        constraintLayoutRedeemPoints = view.findViewById(R.id.constrain_redeem_points)
        constraintLayoutAnotherCard = view.findViewById(R.id.constrain_try_another_card)
        constraintLayoutAnotherNumber = view.findViewById(R.id.constrain_try_another_number)
        constraintLayoutCheckingPoints = view.findViewById(R.id.constrain_checking_points)
        constraintLayoutSavedCard = view.findViewById(R.id.constrain_saved_card_selector)
        textTryAnotherNumber =
            constraintLayoutAnotherNumber.findViewById(R.id.txt_try_another_number)
        textRbiGuideline = view.findViewById(R.id.txt_rbi_guideline)
        textRbiGuideline.setOnClickListener {
            showSavedCardDialog(null)
        }

        checkBoxSavedCard = view.findViewById(R.id.cb_saved_card)
        checkBoxSavedCard.isEnabled = false
        checkBoxSavedCard.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isSavedCardChecked = true
            } else {
                isSavedCardChecked = false
            }
        }
        linearCheckPoint = view.findViewById(R.id.linear_check_points)
        textCheckPoints = linearCheckPoint.findViewById(R.id.txt_check_points)
    }

    private fun updateButtonBackground() {
        if (isCardNumberValid && isExpiryValid && isCVVValid && isCardHolderNameValid) {
            btnProceedToPay.isEnabled = true
            btnProceedToPay.alpha = 1f
            checkBoxSavedCard.isEnabled = true
            //btnProceedToPay.setBackgroundColor(resources.getColor(R.color.header_color))
            //btnProceedToPay.setBackgroundResource(R.color.header_color) // Enabled state with secondary color
        } else {
            btnProceedToPay.isEnabled = false
            btnProceedToPay.alpha = 0.3f
            checkBoxSavedCard.isEnabled = false
            //btnProceedToPay.setBackgroundColor(resources.getColor(R.color.shimmer_grey))
            //btnProceedToPay.setBackgroundResource(R.color.colorPrimary) // Disabled state with primary color
        }
    }

    private fun setCardBrandIcon(etCardNumber: EditText, cardType: String?) {
        val iconResId = cardIcons[cardType]
        if (iconResId != null) {
            etCardNumber.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), iconResId),
                null
            )
        } else {
            // Reset to no drawable if card type doesn't match any known icons
            etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setupCardNumberValidation(etCardNumber: EditText, tvCardNumberError: TextView) {
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (isEditing) return

                isEditing = true

                cleanedInput = s?.toString()?.replace(Regex("\\s+"), "") ?: ""
                val formattedInput = cleanedInput.chunked(4).joinToString(" ")

                //Native otp
                /*if (cleanedInput.length >= 12) {
                    getBinData(token, cleanedInput)
                }*/

                etCardNumber.setText(formattedInput)
                etCardNumber.setSelection(formattedInput.length)

                if (isPBPEnabled)
                    if (cleanedInput.length >= 12) {
                        pbpCardNumber = formattedInput
                        checkReward(formattedInput, null)
                    } else {
                        isPBPChecked = false
                        pbpVisibility(PBP_BANK_VISIBLE)
                    }

                if (cleanedInput.length > 19) {
                    tvCardNumberError.text = "Your card number cannot exceed 19 digits"
                    tvCardNumberError.visibility = View.VISIBLE
                    etCardNumber.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_error_border
                    )
                    isCardNumberValid = false
                } else if (cleanedInput.length < 19) {
                    tvCardNumberError.visibility = View.INVISIBLE
                    etCardNumber.background = setColor(requireActivity())
                    isCardNumberValid = false

                    if (cleanedInput.length > 8) {
                        getBinData(token, cleanedInput)
                    } else {
                        Utils.println("Else condition " + validCard(cleanedInput) + " " + cleanedInput.length + " " + isCardNumberValid)
                    }
                    if (cleanedInput.length <= 4) {
                        etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }

                } else {
                    tvCardNumberError.visibility = View.INVISIBLE
                    etCardNumber.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_default_border
                    )
                    isCardNumberValid = true

                }

                isEditing = false
                updateButtonBackground()
            }
        })
    }

    private fun checkReward(cardNumber: String, mobileNumber: String?) {

        val cardAmount = RewardPointsCardDetails(
            cardNumber.substring(cardNumber.length - 4, cardNumber.length),
            cardNumber.filter { !it.isWhitespace() },
            mobileNumber
        )
        val rewardPaymentOption = RewardPaymentOption(cardAmount)
        val orderDetailsAmount = OrderDetailsAmount(amount!!, "INR")
        val orderDetails = OrderDetails(orderDetailsAmount)
        val rewardRequest = RewardRequest("POINTS", rewardPaymentOption, orderDetails)

        mainViewModel.rewardData(token, rewardRequest)
        mainViewModel.reward_response.removeObservers(this)
        mainViewModel.reward_response.observe(viewLifecycleOwner) { response ->
            val rewardResponseHandler = ApiResultHandler<RewardResponse>(requireActivity(),
                onLoading = {
                    pbpVisibility(PBP_LOADING)
                }, onSuccess = { data ->
                    bottomSheetDialog?.dismiss()
                    if (data!!.is_eligible) {
                        if (data.redeemable_amount != null && data.payment_option_metadata != null) {
                            //data.payment_option_metadata.pay_by_point_option_data.redeemable_points=0
                            if (data.payment_option_metadata.pay_by_point_option_data.redeemable_points == 0) {
                                pbpVisibility(PBP_ZERO_POINTS)
                            } else {
                                redeemableAmount = data!!.redeemable_amount.value
                                pbpVisibility(PBP_POINTS)
                                pbpPoints(
                                    data!!.payment_option_metadata.pay_by_point_option_data.redeemable_points,
                                    data!!.redeemable_amount.value
                                )
                            }
                        } else {
                            pbpVisibility(PBP_CHECK_POINTS)
                        }
                    } else {
                        isPBPChecked = false
                        if (mobileNumber != null && !mobileNumber!!.isEmpty())
                            pbpVisibility(PBP_ANOTHER_MOBILE_NUMBER)
                        else
                            pbpVisibility(PBP_ANOTHER_CARD_NUMBER)
                    }
                    /*Toast.makeText(
                        requireActivity(),
                        data!!.payment_method + " is eligible " + data!!.is_eligible,
                        Toast.LENGTH_SHORT
                    ).show()*/
                }, onFailure = {
                    isPBPChecked = false
                    bottomSheetDialog?.dismiss()
                    pbpVisibility(PBP_CANNOT_CHECK_POINTS)
                }
            )
            rewardResponseHandler.handleApiResult(response)
        }

    }

    private fun setupExpiryValidation(
        etExpiry: EditText,
        tvExpiryError: TextView,
        etCVV: EditText
    ) {
        etExpiry.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Change background to header color while typing
                etExpiry.background = setColor(requireActivity())
            }

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val input = s.toString().replace(Regex("[^\\d]"), "")
                val formattedInput = when {
                    input.length <= 2 -> input
                    input.length <= 4 -> "${input.substring(0, 2)}/${input.substring(2)}"
                    else -> "${input.substring(0, 2)}/${input.substring(2, 4)}"
                }

                etExpiry.setText(formattedInput)
                etExpiry.setSelection(formattedInput.length)

                isEditing = false
            }
        })

        // Focus change listener to validate and update background
        etExpiry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val parts = etExpiry.text.toString().split("/")
                if (parts.size == 2) {
                    val month = parts[0].toIntOrNull()
                    val year = parts[1].toIntOrNull()?.plus(2000)

                    if (month == null || year == null || month !in 1..12) {
                        tvExpiryError.visibility = View.VISIBLE
                        tvExpiryError.text = "Invalid expiry date"
                        etExpiry.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.edittext_error_border
                        )
                        isExpiryValid = false
                    } else {
                        val calendar = Calendar.getInstance()
                        val currentYear = calendar.get(Calendar.YEAR)
                        val currentMonth = calendar.get(Calendar.MONTH) + 1

                        if (year < currentYear || (year == currentYear && month < currentMonth)) {
                            tvExpiryError.visibility = View.VISIBLE
                            tvExpiryError.text = "Card has expired"
                            etExpiry.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.edittext_error_border
                            )
                            isExpiryValid = false
                        } else {
                            tvExpiryError.visibility = View.INVISIBLE
                            etExpiry.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.edittext_default_border
                            )
                            isExpiryValid = true
                        }
                    }
                } else {
                    tvExpiryError.visibility = View.VISIBLE
                    tvExpiryError.text = "Invalid expiry format"
                    etExpiry.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_error_border
                    )
                    isExpiryValid = false
                }

                updateButtonBackground()
            }
        }
    }


    private fun setupCVVValidation(etCVV: EditText, tvCVVError: TextView) {
        etCVV.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(4)) // CVV length is limited to 4 digits

        etCVV.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                isEditing = true

                val cvv = s?.toString() ?: ""

                // Set focused background while typing
                etCVV.background = setColor(requireActivity())

                // Validate input
                if (cvv.length > 4) {
                    tvCVVError.text = "CVV cannot exceed 4 digits"
                    tvCVVError.visibility = View.VISIBLE
                    etCVV.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_error_border
                    )
                    isCVVValid = false
                } else if (cvv.isEmpty() || cvv.length < 4) {
                    tvCVVError.visibility = View.INVISIBLE
                    isCVVValid = false
                } else {
                    tvCVVError.visibility = View.INVISIBLE
                    isCVVValid = true
                }

                isEditing = false
                updateButtonBackground()
            }
        })

        // Focus change listener to validate and set the final background
        etCVV.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cvv = etCVV.text.toString()
                if (cvv.isEmpty() || cvv.length < 3) {
                    tvCVVError.visibility = View.VISIBLE
                    tvCVVError.text = "CVV must be 3 digits"
                    etCVV.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_error_border
                    )
                    isCVVValid = false
                } else {
                    tvCVVError.visibility = View.INVISIBLE
                    etCVV.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_default_border
                    )
                    isCVVValid = true
                }
                updateButtonBackground()
            }
        }
    }

    private fun setupCardHolderNameValidation(etCardHolderName: EditText) {
        etCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val name = s?.toString()?.trim()

                // Allow only alphabets and spaces
                val filteredName = name?.filter { it.isLetter() || it.isWhitespace() }

                if (name != filteredName) {
                    // Replace invalid characters
                    etCardHolderName.setText(filteredName)
                    etCardHolderName.setSelection(filteredName?.length ?: 0)
                }

                // Update the background while typing
                etCardHolderName.background = setColor(requireActivity())

                // Validate the input
                isCardHolderNameValid = !filteredName.isNullOrEmpty()

                // Update button state based on validation
                updateButtonBackground()
            }
        })
    }

    private fun validCard(cardNumber: String): Boolean {
        // Luhn Algorithm to validate the card number
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n -= 9
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    private fun validateCardType(cardNumber: String): String? {
        for ((cardType, regex) in cardTypes) {
            if (regex.matches(cardNumber)) {
                Utils.println("Card type" + cardType)
                return cardType
            }
        }
        return null
    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.process_payment_bottom_sheet, null)

        var logoAnimation: LottieAnimationView = view.findViewById(R.id.img_process_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

        bottomSheetDialog?.setCancelable(false)
        bottomSheetDialog?.setCanceledOnTouchOutside(false)
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
    }

    private fun showPBPMobileNumberDialog() {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.pbp_mobile_number, null)
        val txt_check_point: TextView = view.findViewById(R.id.txt_check_points)
        val progress_check_points: ProgressBar = view.findViewById(R.id.progress_pbp)
        val edt_mobile_number: EditText = view.findViewById(R.id.edt_mobile_number)
        val image_icon: ImageView = view.findViewById(R.id.img_icon)
        val relativeMobile: RelativeLayout = view.findViewById(R.id.layout_mobile_number)
        if (palette != null) {
            val layerDrawable = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.pbp_drawable_layer
            ) as LayerDrawable
            val gradientDrawable =
                layerDrawable.findDrawableByLayerId(R.id.icon_bg) as VectorDrawable
            gradientDrawable.setTint(Color.parseColor(palette?.C900))
            image_icon.setImageDrawable(layerDrawable)
        }
        edt_mobile_number.addTextChangedListener { char ->
            if (char != null) {
                if (char.length > 9) {
                    txt_check_point.isEnabled = true
                    if (palette != null)
                        txt_check_point.setTextColor(Color.parseColor(palette?.C900))
                    else
                        txt_check_point.setTextColor(resources.getColor(R.color.header_color))
                } else {
                    isPBPChecked = false
                    txt_check_point.isEnabled = false
                    txt_check_point.setTextColor(resources.getColor(R.color.shimmer_grey))
                }
                mobileNumber = char.toString()
            }
        }
        edt_mobile_number.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                relativeMobile.background = setColor(requireActivity())
            } else {
                relativeMobile.background = resources.getDrawable(R.drawable.addcardcurved)
            }
        }
        edt_mobile_number.setText(mobileNumber)
        val img_close: ImageView = view.findViewById(R.id.img_close)
        img_close.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        txt_check_point.setOnClickListener {
            mobileNumber = edt_mobile_number.text.toString()
            txt_check_point.text = "Checking points"
            txt_check_point.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            progress_check_points.visibility = View.VISIBLE
            checkReward(etCardNumber.text.toString(), edt_mobile_number.text.toString())
        }
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog?.show()
    }

    fun getBankList(): List<PBPBank> {
        val pbpBankList = ArrayList<PBPBank>()
        pbpBankList.add(PBPBank(BANK_SBI, SBI))
        pbpBankList.add(PBPBank(BANK_PNB, PNB))
        pbpBankList.add(PBPBank(BANK_YES, YES))
        return pbpBankList.toList()
    }

    fun getPBPBankList(): List<PBPBank> {
        val pbpBankList = ArrayList<PBPBank>()
        pbpBankList.add(PBPBank(BANK_ALLAHABAD, ALLAHABAD))
        pbpBankList.add(PBPBank(BANK_ANDHRA, ANDHRA))
        pbpBankList.add(PBPBank(BANK_AU_SMALL, AU))
        //pbpBankList.add(PBPBank(BANK_OF_INDIA, R.drawable.bank_of_india))
        pbpBankList.add(PBPBank(BANK_CANARA, CANARA_BANK))
        pbpBankList.add(PBPBank(BANK_CENTRAL, CENTRAL))
        pbpBankList.add(PBPBank(BANK_CORPORATION, CORPORATION))
        pbpBankList.add(PBPBank(BANK_FEDERAL, FEDERAL))
        pbpBankList.add(PBPBank(BANK_IDBI, IDBI))
        pbpBankList.add(PBPBank(BANK_IDFC, IDFC))
        pbpBankList.add(PBPBank(BANK_IDIAN, INDIAN))
        pbpBankList.add(PBPBank(BANK_KARUR_VYSYA, KARUR))
        pbpBankList.add(PBPBank(BANK_PUNJAB, PNB))
        pbpBankList.add(PBPBank(BANK_SOUTH_INDIAN, SOUTH_INDIAN_BANK))
        pbpBankList.add(PBPBank(BANK_STATE_BANK, SBI))
        pbpBankList.add(PBPBank(BANK_UNION, UNION_BANK))
        pbpBankList.add(PBPBank(BANK_YES_BANK, YES))
        return pbpBankList.toList()
    }

    private fun showMoreBankDialog() {
        val view: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.pbp_more_banks_banner, null)
        val recyclerView: RecyclerView = view.findViewById(R.id.bank_list)
        val close: ImageView = view.findViewById(R.id.img_close)
        close.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = PBPBanksAdapter(getPBPBankList())
        recyclerView.adapter = adapter
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
    }

    private fun pbpBankList() {
        pbpVisibility(PBP_BANK_VISIBLE)
        constraintLayoutPBPBanner.setOnClickListener {
            showMoreBankDialog()
        }
        val layoutManager = FlexboxLayoutManager(requireActivity()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
        }
        recyclerView.layoutManager = layoutManager

        val mNoOfColumns: Int = ColumnUtil.calculateNoOfColumns(requireActivity(), 160F)
        Utils.println("No of columns ${mNoOfColumns}")

        val myRecyclerViewAdapter = FlexAdapter(getBankList(), mNoOfColumns)
        recyclerView.adapter = myRecyclerViewAdapter
    }

    fun pbpVisibility(state: String) {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE

        when (state) {
            PBP_BANK_VISIBLE -> constraintLayoutPBPBanner.visibility = View.VISIBLE
            PBP_LOADING -> constraintLayoutCheckingPoints.visibility = View.VISIBLE
            PBP_CANNOT_CHECK_POINTS -> constraintLayoutCannotCheck.visibility = View.VISIBLE
            PBP_ANOTHER_MOBILE_NUMBER -> constraintLayoutAnotherNumber.visibility = View.VISIBLE
            PBP_ANOTHER_CARD_NUMBER -> constraintLayoutAnotherCard.visibility = View.VISIBLE
            PBP_CHECK_POINTS -> {
                linearCheckPoint.visibility = View.VISIBLE
                if (palette != null) {
                    textCheckPoints.setTextColor(Color.parseColor(palette?.C900))

                    val layerDrawable = linearCheckPoint.background as LayerDrawable
                    val gradientDrawable =
                        layerDrawable.findDrawableByLayerId(R.id.header) as GradientDrawable
                    gradientDrawable.setColor(Color.parseColor(palette?.C900))
                }
            }

            PBP_ZERO_POINTS -> {
                constraintLayoutNoBalance.visibility = View.VISIBLE
                if (palette != null) {
                    val icon: ImageView = constraintLayoutNoBalance.findViewById(R.id.img_pbp)
                    val layerDrawable = ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.pbp_no_balance_layerlist
                    ) as LayerDrawable
                    val gradientDrawable =
                        layerDrawable.findDrawableByLayerId(R.id.icon_bg) as VectorDrawable
                    gradientDrawable.setTint(Color.parseColor(palette?.C900))
                    icon.setImageDrawable(layerDrawable)
                }
            }

            PBP_POINTS -> constraintLayoutRedeemPoints.visibility = View.VISIBLE

        }
    }

    fun allViewsInvisible() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun pbpPoints(redeemablePoints: Int, redeemableAmount: Int) {
        if (palette != null) {
            val icon: ImageView = constraintLayoutRedeemPoints.findViewById(R.id.img_pbp)
            val layerDrawable = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.pbp_no_balance_layerlist
            ) as LayerDrawable
            val gradientDrawable =
                layerDrawable.findDrawableByLayerId(R.id.icon_bg) as VectorDrawable
            gradientDrawable.setTint(Color.parseColor(palette?.C900))
            icon.setImageDrawable(layerDrawable)
        }
        val checkPBP: CheckBox = constraintLayoutRedeemPoints.findViewById(R.id.checkbox_pbp)
        checkPBP.setOnCheckedChangeListener { _, pbpEnabled ->
            if (pbpEnabled) {
                isPBPChecked = true
                val payableAmount = amount!! - redeemableAmount
                mainViewModel.pbpAmount.value = payableAmount
            } else {
                isPBPChecked = false
                mainViewModel.pbpAmount.value = null
            }
        }
        val txtCardPoint: TextView =
            constraintLayoutRedeemPoints.findViewById(R.id.txt_redeem_point)
        val txtInstantDiscount: TextView =
            constraintLayoutRedeemPoints.findViewById(R.id.txt_instant_discount)
        txtCardPoint.text =
            resources.getString(R.string.redeem) + " " + redeemablePoints + " " + resources.getString(
                R.string.card_points
            )

        val redeemAmountString =
            resources.getString(R.string.get) + " " + convertToRupees(
                requireContext(),
                redeemableAmount
            ) + " " + resources.getString(
                R.string.instant_discount
            )

        val spannable: Spannable = SpannableString(redeemAmountString)

        val start =
            redeemAmountString.indexOf(resources.getString(R.string.rs))
        val end = start + convertToRupees(
            requireContext(),
            redeemableAmount
        ).length
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.green)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtInstantDiscount.setText(spannable, TextView.BufferType.SPANNABLE)
        /*val payableAmount = amount!! - redeemableAmount
        mainViewModel.updateAmount(payableAmount)
        amount = payableAmount
        println("Start ${start} Finish ${end} Amount ${payableAmount}")*/
    }

    private fun makeViewVisible() {
        pbpBankList()
    }

    private fun observeListener() {

        try {

            savedCardViewModel.skipSavedCard.observe(viewLifecycleOwner) { skipSavedCard ->
                this.skipSavedCard = skipSavedCard
                checkBoxSavedCard.isChecked = false
            }

            savedCardViewModel.saved_card_request_otp_response.observe(viewLifecycleOwner) { response ->
                val responseHandler =
                    ApiResultHandler<SavedCardResponse>(requireActivity(), onLoading = {
                    }, onSuccess = { data ->

                        if (paymentId == null && buttonClicked) {
                            savedCardViewModel.otpId.value = data?.otpId

                            if (bottomSheetSavedCardInfo?.isVisible == true) bottomSheetSavedCardInfo?.dismiss()

                            if (!::bottomSheetOtp.isInitialized || !bottomSheetOtp.isVisible) {

                                val argument = Bundle()
                                argument.putString(MOBILE, mobileNumber)
                                argument.putString(TOKEN, token)
                                argument.putString(CUSTOMER_ID, customerId)
                                argument.putString(OTP_ID, data?.otpId)

                                bottomSheetOtp = BottomSheetOtp(palette)
                                bottomSheetOtp.arguments = argument

                                bottomSheetOtp.isCancelable = false
                                bottomSheetOtp.show(requireActivity().supportFragmentManager, "")
                            }

                            Utils.println("Otp response ${data?.otpId}}")
                        }

                    }, onFailure = { errorMessage ->

                    })
                responseHandler.handleApiResult(response)
            }

            savedCardViewModel.saved_card_validate_otp_response.observe(viewLifecycleOwner) { response ->
                val responseHandler =
                    ApiResultHandler<SavedCardResponse>(requireActivity(), onLoading = {
                    }, onSuccess = { data ->
                        if (paymentId == null && buttonClicked) {
                            buttonClicked = true
                            // Handle payment process here
                            /*val cardNumber =
                                etCardNumber.text.toString().filter { !it.isWhitespace() }
                            CleverTapUtil.CT_EVENT_PAYMENT_METHOD(
                                clevertapDefaultInstance, CT_CARDS, PAYMENT_INITIATED,
                                cardNumber, null, null
                            )
                            val cvv = etCVV.text.toString()
                            val cardHolderName = etCardHolderName.text.toString()
                            val cardExpiry = etExpiry.text.toString()
                            val cardExpiryMonth = cardExpiry.split("/")[0]
                            val cardExpiryYear = "20" + cardExpiry.split("/")[1]
                            payAction(
                                cardNumber,
                                cvv,
                                cardHolderName,
                                cardExpiryMonth,
                                cardExpiryYear,
                                redeemableAmount,
                                mobileNumber,
                                isNativeOTP,
                                true
                            )*/

                            isSaveCard = true
                            val processPaymentRequest = createProcessPaymentRequest()
                            mainViewModel.processPayment(token, processPaymentRequest)
                        }

                    }, onFailure = { errorMessage ->

                    })
                responseHandler.handleApiResult(response)
            }

            mainViewModel.pbpAmount.distinctUntilChanged()
                .observe(viewLifecycleOwner) { pbpAmount ->
                    if (pbpAmount != null) {
                        val redeemableAmount = amount!! - redeemableAmount!!
                        buttonText = getString(R.string.pay) + " " + convertToRupees(
                            requireContext(),
                            redeemableAmount
                        )
                        btnProceedToPay.text = buttonText
                    } else {
                        buttonText = getString(R.string.pay) + " " + convertToRupees(
                            requireContext(),
                            amount!!
                        )
                        btnProceedToPay.text = buttonText
                    }
                }

            mainViewModel.fetch_data_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    val fetchDataResponseHandler =
                        ApiResultHandler<FetchResponse>(requireActivity(),
                            onLoading = {
                            }, onSuccess = { data ->
                                amount = data!!.paymentData!!.originalTxnAmount?.amount
                                currency = data!!.paymentData!!.originalTxnAmount?.currency
                                palette = data?.merchantBrandingData?.palette
                                if (palette != null) {
                                    textRbiGuideline.setTextColor(Color.parseColor(palette?.C900))
                                }
                                orderId = data?.transactionInfo?.orderId
                                customerId =
                                    if (data?.customerInfo?.customerId != null) data?.customerInfo?.customerId else data?.customerInfo?.customer_id
                                mobileNumber = data?.customerInfo?.mobileNo
                                btnProceedToPay.background =
                                    buttonBackground(requireActivity(), palette)
                                if (data.customerInfo != null && data.customerInfo.mobileNo != null)
                                    mobileNumber = data.customerInfo.mobileNo
                                data?.dccData.let { dccList ->
                                    this.dccList = dccList
                                }
                                buttonText = getString(R.string.pay) + " " + convertToRupees(
                                    requireContext(),
                                    amount!!
                                )
                                btnProceedToPay.text = buttonText
                                val paymentModeData = data!!.paymentModes!!.filter { paymentMode ->
                                    paymentMode.paymentModeId.equals(
                                        PAYBYPOINTS_ID
                                    )
                                }
                                if (paymentModeData.size > 0) {
                                    makeViewVisible()
                                    isPBPEnabled = true
                                }

                                /*data?.merchantInfo?.featureFlags?.let { flag ->
                                    if (flag?.isSavedCardEnabled != null && flag?.isSavedCardEnabled!! && mobileNumber != null && data?.customerInfo?.isEditCustomerDetailsAllowed == true) {
                                        constraintLayoutSavedCard.visibility = View.VISIBLE
                                        isSavedCardEnabled = true
                                    }
                                    if (flag?.isDCCEnabled == true) isDCCEnabled = true
                                }*/
                            }, onFailure = {}
                        )
                    fetchDataResponseHandler.handleApiResult(response)
                }


            /*val fetchData =
                ApiResultHandler<ProcessPaymentResponse>(requireActivity(), onLoading = {

                }, onSuccess = {

                }, onFailure = {

                })
            fetchData.handleApiResult(mainViewModel?.process_payment_data)*/

            mainViewModel.process_payment_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    if (buttonClicked) {
                        startTime = System.currentTimeMillis()
                        val fetchDataResponseHandler =
                            ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                                onLoading = {
                                    if (::bottomSheetOtp.isInitialized && bottomSheetOtp.isVisible) bottomSheetOtp.dismiss()
                                    if (bottomSheetSavedCardInfo?.isVisible == true) bottomSheetSavedCardInfo?.dismiss()

                                    showProcessPaymentDialog()
                                }, onSuccess = { data ->

                                    //LandingActivity().paymentId = data?.payment_id
                                    mainViewModel.paymentId.value = data?.payment_id
                                    paymentId = data?.payment_id
                                    redirectUrl = data?.redirect_url
                                    bottomSheetDialog?.findViewById<LottieAnimationView>(R.id.img_process_logo)!!
                                        .addAnimatorListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(p0: Animator) {
                                            }

                                            override fun onAnimationEnd(p0: Animator) {
                                            }

                                            override fun onAnimationCancel(p0: Animator) {
                                            }

                                            override fun onAnimationRepeat(p0: Animator) {

                                                if (isNativeOTP == true) {
                                                    generateOtp(
                                                        data?.payment_id
                                                    )
                                                } else {
                                                    bottomSheetDialog?.dismiss()
                                                    redirectToACS(
                                                        startTime,
                                                        data?.redirect_url,
                                                        data?.order_id,
                                                        data?.payment_id
                                                    )
                                                }

                                                /*val i = Intent(activity, ACSPageActivity::class.java)
                                                i.putExtra(TOKEN, token)
                                                i.putExtra(START_TIME, startTime)
                                                i.putExtra(REDIRECT_URL, data!!.redirect_url)
                                                i.putExtra(ORDER_ID, data?.order_id)
                                                i.putExtra(PAYMENT_ID, data?.payment_id)
                                                requireActivity().startActivityForResult(
                                                    i,
                                                    REQ_RETRY_CALLBACK
                                                )*/
                                                //requireActivity().finish()
                                                //generateOtp(data?.payment_id, data?.redirect_url)
                                            }

                                        })

                                }, onFailure = { errorMessage ->

                                    bottomSheetDialog?.dismiss()
                                    /*val i = Intent(activity, ACSPageActivity::class.java)
                                    i.putExtra(TOKEN, token)
                                    i.putExtra(START_TIME, startTime)
                                    i.putExtra(REDIRECT_URL, "www.google.com")
                                    i.putExtra(ORDER_ID, "123")
                                    i.putExtra(PAYMENT_ID, "123")
                                    requireActivity().startActivityForResult(
                                        i,
                                        REQ_RETRY_CALLBACK
                                    )
                                    requireActivity().finish()*/

                                    listener?.onRetry(
                                        false,
                                        errorMessage?.error_code,
                                        errorMessage?.error_message
                                    )
                                    /*bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_logo)!!
                                        .addAnimatorListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(p0: Animator) {
                                            }

                                            override fun onAnimationEnd(p0: Animator) {
                                                Toast.makeText(activity, "Ended", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onAnimationCancel(p0: Animator) {
                                            }

                                            override fun onAnimationRepeat(p0: Animator) {
                                                bottomSheetDialog.dismiss()
                                                *//*val i = Intent(requireActivity(), FailureActivity::class.java)
                                        i.putExtra(ORDER_ID, orderId)
                                        i.putExtra(ERROR_CODE, errorMessage?.error_code)
                                        i.putExtra(ERROR_MESSAGE, errorMessage?.error_message)
                                        startActivity(i)
                                        requireActivity().finish()*//*

                                        //requireActivity().finish()
                                    }

                                })*/
                                })
                        fetchDataResponseHandler.handleApiResult(response)

                    }
                }

            mainViewModel.bin_data_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    val fetchDataResponseHandler =
                        ApiResultHandler<CardBinMetaDataResponse>(requireActivity(),
                            onLoading = {},
                            onSuccess = { data ->
                                //FORCE_OPT_OUT data?.card_payment_details?.get(0)?.is_currency_supported = false
                                //NATIVE- data?.card_payment_details?.get(0)?.is_native_otp_supported=true
                                Utils.println("Domestic card ${data?.card_payment_details?.get(0)?.card_network}")
                                isNativeOTP =
                                    data?.card_payment_details?.get(0)?.is_native_otp_supported
                                setCardBrandIcon(
                                    etCardNumber,
                                    data?.card_payment_details?.get(0)?.card_network
                                )
                                binData = data?.card_payment_details?.get(0)
                                if (isDCCEnabled && data?.card_payment_details?.get(0)?.is_international_card == true && data?.card_payment_details?.get(
                                        0
                                    )?.is_currency_supported == true
                                ) {
                                    isDCCEligible = true
                                    btnProceedToPay.text = getString(R.string.continue_)
                                } else {
                                    isDCCEligible = false
                                    btnProceedToPay.text = buttonText
                                }

                            },
                            onFailure = {
                                /*val data = CardBinMetaDataResponseData(
                                    "",
                                    "",
                                    "VISA",
                                    "INTL HDQTRS-CENTER OWNED",
                                    "DEBIT",
                                    "CONSUMER",
                                    false,
                                    true,
                                    "USA",
                                    "USD",
                                    true,
                                    246,
                                    0.246890,
                                    9
                                )
                                binData = data
                                if (isDCCEnabled && data?.is_international_card == true && data?.is_currency_supported == true
                                ) {
                                    isDCCEligible = true
                                    btnProceedToPay.text = getString(R.string.continue_)
                                } else {
                                    isDCCEligible = false
                                    btnProceedToPay.text = buttonText
                                }*/
                                try {
                                    if (cleanedInput != null) {
                                        val cardType = validateCardType(cleanedInput)
                                        Utils.println("Valid condition " + validCard(cleanedInput) + " " + cleanedInput.length + " " + isCardNumberValid)
                                        // Set the brand icon based on the card type
                                        setCardBrandIcon(etCardNumber, cardType)
                                    }
                                } catch (e: Exception) {

                                }

                            })
                    fetchDataResponseHandler.handleApiResult(response)
                }

            mainViewModel.generate_otp_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    if (buttonClicked) {
                        val generateOtpResponseHandler =
                            ApiResultHandler<OTPResponse>(requireActivity(),
                                onLoading = {
                                },
                                onSuccess = { data ->
                                    bottomSheetDialog?.dismiss()

                                    val arguments = Bundle()
                                    arguments.putString(TOKEN, token)
                                    arguments.putString(PAYMENT_ID, paymentId)
                                    arguments.putString(ORDER_ID, orderId)
                                    data?.meta_data?.let { metaData ->
                                        arguments.putString(
                                            RESEND_TIMER,
                                            data?.meta_data?.resend_after
                                        )
                                    }
                                    data?.next?.contains(OTP_RESEND)
                                        ?.let { arguments.putBoolean(OTP_RESEND, it) }
                                    arguments.putParcelable(
                                        PROCESS_PAYMENT_REQUEST,
                                        processPaymentRequest
                                    )

                                    val optFragment = OtpFragment()
                                    optFragment.arguments = arguments

                                    val transaction =
                                        requireActivity().supportFragmentManager.beginTransaction()
                                    transaction.replace(
                                        R.id.details_fragment,
                                        optFragment,
                                        TAG_OTP
                                    )
                                    transaction.addToBackStack(TAG_OTP)
                                    transaction.commit()

                                },
                                onFailure = {
                                    bottomSheetDialog?.dismiss()
                                    redirectToACS(
                                        startTime,
                                        redirectUrl,
                                        orderId,
                                        paymentId
                                    )
                                })
                        generateOtpResponseHandler.handleApiResult(response)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun getBinData(token: String, cardNumber: String) {
        val binRequest = CardBinMetaDataRequest(cardNumber, PAYMENT_REFERENCE_TYPE_CARD)
        val binRequestList = arrayListOf<CardBinMetaDataRequest>()
        binRequestList.add(binRequest)
        Utils.println("bin request " + Gson().toJson(binRequestList))
        val cardBinData = CardBinMetaDataRequestList(card_details = binRequestList)
        if (isDCCEnabled) {
            cardBinData.amount = amount
            cardBinData.markup_required = true
            cardBinData.dcc_details_required = true
        }
        mainViewModel.getBinData(token, cardBinData)
    }

    private fun generateOtp(
        paymentId: String?
    ) {
        val otpRequest = OTPRequest(payment_id = paymentId)
        mainViewModel.generatOtp(token, otpRequest)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.process_payment_response.removeObservers(requireActivity())
        savedCardViewModel.saved_card_validate_otp_response.removeObservers(requireActivity())
        savedCardViewModel.saved_card_request_otp_response.removeObservers(requireActivity())
    }

    fun redirectToACS(
        startTime: Long?,
        redirectUrl: String?,
        orderId: String?,
        paymentId: String?
    ) {

        val arguments = Bundle()
        arguments.putString(TOKEN, token)
        arguments.putLong(START_TIME, startTime!!)
        arguments.putString(
            REDIRECT_URL,
            redirectUrl
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

    }

    fun reset() {
        etCardNumber.setText("")
        etExpiry.setText("")
        etCVV.setText("")
        etCardHolderName.setText("")
    }

    override fun onResume() {
        super.onResume()
        //reset()
    }

    override fun onPause() {
        super.onPause()
        //onCreate(null)
        //reset()
    }

    fun showSavedCardDialog(bundle: Bundle?) {
        bottomSheetSavedCardInfo = BottomSheetSavedCard(palette)
        bottomSheetSavedCardInfo?.arguments = bundle

        bottomSheetSavedCardInfo?.isCancelable = false
        bottomSheetSavedCardInfo?.show(requireActivity().supportFragmentManager, "")
    }

    fun showDCCDialog(bindata: CardBinMetaDataResponseData?, dccData: DccData?) {
        val bundle = Bundle()
        bundle.putParcelable(DCC_DATA, dccData)
        bundle.putParcelable(BIN_DATA, bindata)
        amount?.let { bundle.putInt(AMOUNT, it) }
        bundle.putString(TOKEN, token)
        bundle.putParcelable(PROCESS_PAYMENT_REQUEST, processPaymentRequest)
        bundle.putParcelable(PALETTE, palette)

        bottomSheetDccDialog = BottomSheetDcc()
        bottomSheetDccDialog?.arguments = bundle

        bottomSheetDccDialog?.isCancelable = false
        bottomSheetDccDialog?.show(requireActivity().supportFragmentManager, "")
    }

    fun createProcessPaymentRequest(): ProcessPaymentRequest {

        val cardNumber = etCardNumber.text.toString().filter { !it.isWhitespace() }
        val cvv = etCVV.text.toString()
        val cardHolderName = etCardHolderName.text.toString()
        val cardExpiry = etExpiry.text.toString()
        val cardExpiryMonth = cardExpiry.split("/")[0]
        val cardExpiryYear = "20" + cardExpiry.split("/")[1]

        CleverTapUtil.CT_EVENT_PAYMENT_METHOD(
            clevertapDefaultInstance, CT_CARDS, PAYMENT_INITIATED,
            cardNumber, null, null
        )

        val paymentMode = arrayListOf<String>()
        paymentMode.add(CREDIT_DEBIT_ID)
        if (isPBPChecked) {
            paymentMode.add(PAYBYPOINTS_ID)
            amount = amount!! - redeemableAmount!!
        }
        val last4 = cardNumber.substring(cardNumber.length - 4, cardNumber.length)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val pixelFormat = requireActivity().windowManager.defaultDisplay.pixelFormat
        var dccStatus: String? =
            if (isDCCEnabled && binData?.is_international_card == true && binData?.is_currency_supported == false)
                DCC_STATUS.FORCE_OPT_OUT.toString()
            else null

        val screenSize: String = width.toString() + "x" + height.toString()
        val deviceInfo = DeviceInfo(
            null,
            BROWSER_USER_AGENT_ANDROID,
            BROWSER_ACCEPT_ALL,
            Locale.getDefault().language,
            height.toString(),
            width.toString(),
            Utils.getTimeOffset().toString(),
            screenSize,
            Utils.getColorDepth(pixelFormat).toString(),
            true, true, DeviceUtil.getDeviceId(requireActivity()),
            Utils.getLocalIpAddress().toString()
        )

        val cardDataExtra =
            Extra(
                paymentMode,
                amount,
                currency,
                last4,
                redeemableAmount,
                null
                /*mobileNumber!!.filter { !mobileNumber!!.isEmpty() }*/,
                null,
                deviceInfo,
                null,
                dccStatus,
                Utils.createSDKData(requireActivity())
            )
        val cardData =
            CardData(
                cardNumber,
                cvv,
                cardHolderName,
                cardExpiryYear,
                cardExpiryMonth,
                isNativeOTP,
                if (skipSavedCard) false else isSaveCard
            )
        processPaymentRequest =
            ProcessPaymentRequest(
                null,
                null,
                cardData,
                upi_data = null,
                null,
                null,
                cardDataExtra,
                null,
                null
            )
        return processPaymentRequest
    }

}
