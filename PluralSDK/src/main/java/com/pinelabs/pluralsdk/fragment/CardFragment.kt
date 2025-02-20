package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
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
import android.widget.Toast
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
import com.google.gson.Gson
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.ACSPageActivity
import com.pinelabs.pluralsdk.activity.LandingActivity
import com.pinelabs.pluralsdk.adapter.FlexAdapter
import com.pinelabs.pluralsdk.adapter.PBPBanksAdapter
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequest
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequestList
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponse
import com.pinelabs.pluralsdk.data.model.CardData
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
import com.pinelabs.pluralsdk.data.utils.AmountUtil.convertToRupees
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.ColumnUtil
import com.pinelabs.pluralsdk.data.utils.Utils
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
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_ACCEPT_ALL
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_ACCEPT_HEADER
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_IP_ADDRESS
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_JAVASCRIPT_ENABLED
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_LANGUAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_SCREEN_COLOR_DEPTH
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_SCREEN_HEIGHT
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_SCREEN_WIDTH
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_TIME_ZONE
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_USER_AGENT
import com.pinelabs.pluralsdk.utils.Constants.Companion.BROWSER_USER_AGENT_ANDROID
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_CARDS
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_CODE
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_INITIATED
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_REFERENCE_TYPE_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.REQ_RETRY_CALLBACK
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_OTP
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_PAYMENT_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.PaymentModes
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
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
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBanks: BottomSheetDialog
    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private var isCardNumberValid = false
    private var isExpiryValid = false
    private var isCVVValid = false
    private var isCardHolderNameValid = false
    private var isPBPChecked = false
    private var isPBPEnabled = false
    private var orderId: String? = null
    private var palette: Palette? = null

    private lateinit var etCardNumber: EditText
    private lateinit var btnProceedToPay: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var constraintLayoutPBPBanner: ConstraintLayout
    private lateinit var constraintLayoutCannotCheck: ConstraintLayout
    private lateinit var constraintLayoutAnotherNumber: ConstraintLayout
    private lateinit var constraintLayoutAnotherCard: ConstraintLayout
    private lateinit var constraintLayoutNoBalance: ConstraintLayout
    private lateinit var constraintLayoutRedeemPoints: ConstraintLayout
    private lateinit var constraintLayoutCheckingPoints: ConstraintLayout
    private lateinit var linearCheckPoint: LinearLayout
    private lateinit var textCheckPoints: TextView
    private lateinit var textTryAnotherNumber: TextView

    private lateinit var spannableString: SpannableString

    private var clevertapDefaultInstance: CleverTapAPI? = null

    private var listener: onRetryListener? = null
    private var buttonClicked: Boolean = false

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


        buttonClicked = false
        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.layout_orginal)
        activityButton.visibility = View.VISIBLE

        println("Require activity name ${requireActivity().packageName}")
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())

        token = arguments?.getString(TOKEN).toString()
        bottomSheetDialog = BottomSheetDialog(requireActivity())

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
        val etExpiry: EditText = view.findViewById(R.id.etExpiry)
        val tvExpiryError: TextView = view.findViewById(R.id.tvExpiryError)
        val etCardHolderName: EditText = view.findViewById(R.id.etCardHolderName)
        val etCVV: EditText = view.findViewById(R.id.etCVV)
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
            val cardNumber = etCardNumber.text.toString().filter { !it.isWhitespace() }
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
                mobileNumber
            )
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
        textTryAnotherNumber =
            constraintLayoutAnotherNumber.findViewById(R.id.txt_try_another_number)
        linearCheckPoint = view.findViewById(R.id.linear_check_points)
        textCheckPoints = linearCheckPoint.findViewById(R.id.txt_check_points)
    }

    private fun updateButtonBackground() {
        if (isCardNumberValid && isExpiryValid && isCVVValid && isCardHolderNameValid) {
            btnProceedToPay.isEnabled = true
            btnProceedToPay.alpha = 1f
            //btnProceedToPay.setBackgroundColor(resources.getColor(R.color.header_color))
            //btnProceedToPay.setBackgroundResource(R.color.header_color) // Enabled state with secondary color
        } else {
            btnProceedToPay.isEnabled = false
            btnProceedToPay.alpha = 0.3f
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

                val cleanedInput = s?.toString()?.replace(Regex("\\s+"), "") ?: ""
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
                        pbpBankVisbile()
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

                    if (cleanedInput.length > 9) {
                        val cardType = validateCardType(cleanedInput)
                        println("Valid condition " + validCard(cleanedInput) + " " + cleanedInput.length + " " + isCardNumberValid)


                        // Set the brand icon based on the card type
                        setCardBrandIcon(etCardNumber, cardType)
                    } else {
                        println("Else condition " + validCard(cleanedInput) + " " + cleanedInput.length + " " + isCardNumberValid)
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

        //token = "S01D5yKritUp4KsG4IVJWyGpa1YQ3A0I6WOqLNdyr%2F6fMc%3D"
        mainViewModel.rewardData(token, rewardRequest)
        mainViewModel.reward_response.removeObservers(this)
        mainViewModel.reward_response.observe(viewLifecycleOwner) { response ->
            val rewardResponseHandler = ApiResultHandler<RewardResponse>(requireActivity(),
                onLoading = {
                    LoadingVisbile()
                }, onSuccess = { data ->
                    bottomSheetDialog.dismiss()
                    if (data!!.is_eligible) {
                        if (data.redeemable_amount != null && data.payment_option_metadata != null) {
                            //data.payment_option_metadata.pay_by_point_option_data.redeemable_points=0
                            if (data.payment_option_metadata.pay_by_point_option_data.redeemable_points == 0) {
                                zeroPoints()
                            } else {
                                redeemableAmount = data!!.redeemable_amount.value
                                pbpPoints(
                                    data!!.payment_option_metadata.pay_by_point_option_data.redeemable_points,
                                    data!!.redeemable_amount.value
                                )
                            }
                        } else {
                            checkPoints()
                        }
                    } else {
                        isPBPChecked = false
                        if (mobileNumber != null && !mobileNumber!!.isEmpty())
                            anotherMobileNumber()
                        else
                            anotherCardNumber()
                    }
                    /*Toast.makeText(
                        requireActivity(),
                        data!!.payment_method + " is eligible " + data!!.is_eligible,
                        Toast.LENGTH_SHORT
                    ).show()*/
                }, onFailure = {
                    isPBPChecked = false
                    bottomSheetDialog.dismiss()
                    unableToCheckPoints()
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
                println("Card type" + cardType)
                return cardType
            }
        }
        return null
    }

    private fun payAction(
        cardNumber: String,
        cvv: String,
        cardHolderName: String,
        cardExpiryMonth: String,
        cardExpiryYear: String,
        redeemableAmount: Int?,
        mobileNumber: String?
    ) {
        val paymentMode = arrayListOf<String>()
        paymentMode.add(CREDIT_DEBIT_ID)
        if (isPBPChecked) {
            paymentMode.add(PAYBYPOINTS_ID)
            amount = amount!! - redeemableAmount!!
        }
        val last4 = cardNumber.substring(cardNumber.length - 4, cardNumber.length)

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
                null
            )
        val cardData = CardData(cardNumber, cvv, cardHolderName, cardExpiryYear, cardExpiryMonth)
        val processPaymentRequest =
            ProcessPaymentRequest(cardData, upi_data = null, null, cardDataExtra, null, null)
        mainViewModel.processPayment(token, processPaymentRequest)

    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.process_payment_bottom_sheet, null)

        var logoAnimation: LottieAnimationView = view.findViewById(R.id.img_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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
            bottomSheetDialog.dismiss()
        }
        txt_check_point.setOnClickListener {
            mobileNumber = edt_mobile_number.text.toString()
            txt_check_point.text = "Checking points"
            txt_check_point.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            progress_check_points.visibility = View.VISIBLE
            checkReward(etCardNumber.text.toString(), edt_mobile_number.text.toString())
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
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
            bottomSheetDialog.dismiss()
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = PBPBanksAdapter(getPBPBankList())
        recyclerView.adapter = adapter
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun pbpBankList() {
        pbpBankVisbile()
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
        println("No of columns ${mNoOfColumns}")

        val myRecyclerViewAdapter = FlexAdapter(getBankList(), mNoOfColumns)
        recyclerView.adapter = myRecyclerViewAdapter
    }

    fun pbpBankVisbile() {
        constraintLayoutPBPBanner.visibility = View.VISIBLE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun LoadingVisbile() {
        constraintLayoutCheckingPoints.visibility = View.VISIBLE
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
    }

    fun unableToCheckPoints() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.VISIBLE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun checkPoints() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.VISIBLE
        if (palette != null) {
            textCheckPoints.setTextColor(Color.parseColor(palette?.C900))

            val layerDrawable = linearCheckPoint.background as LayerDrawable
            val gradientDrawable =
                layerDrawable.findDrawableByLayerId(R.id.header) as GradientDrawable
            gradientDrawable.setColor(Color.parseColor(palette?.C900))
        }
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun anotherMobileNumber() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.VISIBLE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun anotherCardNumber() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.VISIBLE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
    }

    fun zeroPoints() {
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
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
        constraintLayoutRedeemPoints.visibility = View.GONE
        constraintLayoutCheckingPoints.visibility = View.GONE
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
        constraintLayoutCheckingPoints.visibility = View.GONE
        constraintLayoutPBPBanner.visibility = View.GONE
        linearCheckPoint.visibility = View.GONE
        constraintLayoutCannotCheck.visibility = View.GONE
        constraintLayoutAnotherNumber.visibility = View.GONE
        constraintLayoutAnotherCard.visibility = View.GONE
        constraintLayoutNoBalance.visibility = View.GONE
        constraintLayoutRedeemPoints.visibility = View.VISIBLE
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
            mainViewModel.pbpAmount.distinctUntilChanged()
                .observe(viewLifecycleOwner) { pbpAmount ->
                    if (pbpAmount != null) {
                        val redeemableAmount = amount!! - redeemableAmount!!
                        btnProceedToPay.text = getString(R.string.pay) + " " + convertToRupees(
                            requireContext(),
                            redeemableAmount
                        )
                    } else {
                        btnProceedToPay.text =
                            getString(R.string.pay) + " " + convertToRupees(
                                requireContext(),
                                amount!!
                            )
                    }
                }

            mainViewModel.fetch_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    val fetchDataResponseHandler =
                        ApiResultHandler<FetchResponse>(requireActivity(),
                            onLoading = {
                            }, onSuccess = { data ->
                                amount = data!!.paymentData!!.originalTxnAmount?.amount
                                currency = data!!.paymentData!!.originalTxnAmount?.currency
                                palette = data?.merchantBrandingData?.palette
                                orderId = data?.transactionInfo?.orderId
                                btnProceedToPay.background = buttonBackground(requireActivity())
                                if (data.customerInfo != null && data.customerInfo.mobileNo != null)
                                    mobileNumber = data.customerInfo.mobileNo
                                btnProceedToPay.text =
                                    getString(R.string.pay) + " " + convertToRupees(
                                        requireContext(),
                                        amount!!
                                    )
                                val paymentModeData = data!!.paymentModes!!.filter { paymentMode ->
                                    paymentMode.paymentModeId.equals(
                                        PAYBYPOINTS_ID
                                    )
                                }
                                if (paymentModeData.size > 0) {
                                    makeViewVisible()
                                    isPBPEnabled = true
                                }
                            }, onFailure = {}
                        )
                    fetchDataResponseHandler.handleApiResult(response)
                }

            mainViewModel.process_payment_response.distinctUntilChanged()
                .observe(viewLifecycleOwner) { response ->
                    if (buttonClicked) {

                        val startTime = System.currentTimeMillis()
                        val fetchDataResponseHandler =
                            ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                                onLoading = {
                                    showProcessPaymentDialog()
                                }, onSuccess = { data ->
                                    LandingActivity().paymentId = data?.payment_id
                                    bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_logo)!!
                                        .addAnimatorListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(p0: Animator) {
                                            }

                                            override fun onAnimationEnd(p0: Animator) {
                                                Toast.makeText(
                                                    activity,
                                                    "Ended",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }

                                            override fun onAnimationCancel(p0: Animator) {
                                            }

                                            override fun onAnimationRepeat(p0: Animator) {
                                                bottomSheetDialog.dismiss()


                                                val arguments = Bundle()
                                                arguments.putString(TOKEN, token)
                                                arguments.putLong(START_TIME, startTime)
                                                arguments.putString(
                                                    REDIRECT_URL,
                                                    data!!.redirect_url
                                                )
                                                arguments.putString(ORDER_ID, data?.order_id)
                                                arguments.putString(PAYMENT_ID, data?.payment_id)
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

                                    bottomSheetDialog.dismiss()
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

                                    listener?.onRetry(false, errorMessage?.error_message)
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

    private fun getBinData(token: String, cardNumber: String) {
        val binRequest = CardBinMetaDataRequest(cardNumber, PAYMENT_REFERENCE_TYPE_CARD)
        val binRequestList = arrayListOf<CardBinMetaDataRequest>()
        binRequestList.add(binRequest)
        println("bin request " + Gson().toJson(binRequestList))
        mainViewModel.getBinData(token, CardBinMetaDataRequestList(binRequestList))
        mainViewModel.bin_data_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val fetchDataResponseHandler =
                    ApiResultHandler<CardBinMetaDataResponse>(requireActivity(),
                        onLoading = {},
                        onSuccess = { data ->
                            println("Domestic card ${data?.extendedCardMetaResponseList?.get(0)?.cardNetwork}")
                            setCardBrandIcon(
                                etCardNumber,
                                data?.extendedCardMetaResponseList?.get(0)?.cardNetwork
                            )
                        },
                        onFailure = {})
                fetchDataResponseHandler.handleApiResult(response)
            }
    }

    private fun generateOtp(paymentId: String?, challengeUrl: String?) {
        val otpRequest = OTPRequest(paymentId, null, challengeUrl)
        mainViewModel.generatOtp(token, otpRequest)
        mainViewModel.generate_otp_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val generateOtpResponseHandler =
                    ApiResultHandler<OTPResponse>(requireActivity(),
                        onLoading = {},
                        onSuccess = {
                            /*val optFragment = OtpFragment()
                                        val transaction =
                                            requireActivity().supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.details_fragment, optFragment, TAG_OTP)
                                        transaction.commit()*/
                        },
                        onFailure = {})
                generateOtpResponseHandler.handleApiResult(response)
            }
    }

    private fun sendOtp(paymentId: String, otp: String) {
        val otpRequest = OTPRequest(paymentId, otp, null)
        mainViewModel.submitOtp(token, otpRequest)
        mainViewModel.submit_otp_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val sendOtpResponseHandler =
                    ApiResultHandler<OTPResponse>(requireActivity(),
                        onLoading = {},
                        onSuccess = {},
                        onFailure = {})
                sendOtpResponseHandler.handleApiResult(response)
            }
    }

    private fun resendOtp(paymentId: String) {
        val otpRequest = OTPRequest(paymentId, null, null)
        mainViewModel.resendOtp(token, otpRequest)
        mainViewModel.resend_otp_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val resendOtpResponseHandler =
                    ApiResultHandler<OTPResponse>(requireActivity(),
                        onLoading = {},
                        onSuccess = {},
                        onFailure = {})
                resendOtpResponseHandler.handleApiResult(response)
            }
    }

    private fun createPaymentParams(): HashMap<String, String> {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val pixelFormat = requireActivity().windowManager.defaultDisplay.pixelFormat
        println("Screen size " + height + " " + width)
        println("Ip address " + Utils.getLocalIpAddress())

        var browserDetails = HashMap<String, String>()
        browserDetails.put(BROWSER_ACCEPT_HEADER, BROWSER_ACCEPT_ALL)
        browserDetails.put(BROWSER_LANGUAGE, Locale.getDefault().language)
        browserDetails.put(BROWSER_SCREEN_HEIGHT, height.toString())
        browserDetails.put(BROWSER_SCREEN_WIDTH, width.toString())
        browserDetails.put(BROWSER_TIME_ZONE, Utils.getTimeOffset().toString())
        browserDetails.put(
            BROWSER_USER_AGENT,
            BROWSER_USER_AGENT_ANDROID
        )
        browserDetails.put(BROWSER_IP_ADDRESS, Utils.getLocalIpAddress().toString())
        browserDetails.put(BROWSER_SCREEN_COLOR_DEPTH, Utils.getColorDepth(pixelFormat).toString())
        browserDetails.put(BROWSER_JAVASCRIPT_ENABLED, "true")
        return browserDetails
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.process_payment_response.removeObservers(viewLifecycleOwner)
    }
}
