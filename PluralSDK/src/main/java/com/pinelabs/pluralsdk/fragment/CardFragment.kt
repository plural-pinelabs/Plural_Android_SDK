package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pinelabs.pluralsdk.R
import java.util.Calendar
import com.pinelabs.pluralsdk.activity.ACSPageActivity
import com.pinelabs.pluralsdk.activity.FailureActivity
import com.pinelabs.pluralsdk.data.model.CardData
import com.pinelabs.pluralsdk.data.model.CardDataExtra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_REF
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel

class CardFragment : Fragment() {

    private val cardTypes = mapOf(
        "amex" to "^3[47]\\d{13}$".toRegex(),
        "BCGlobal" to "^(6541|6556)\\d{12}$".toRegex(),
        "Carte Blanche" to "^389\\d{11}$".toRegex(),
        "Diners Club" to "^3(?:0[0-5]|[68]\\d)\\d{11}$".toRegex(),
        "Discover" to "^(6[54][4-9]\\d{12}|622(12[6-9]|1[3-9]\\d|[2-8]\\d\\d|9[01]\\d|92[0-5])\\d{10})$".toRegex(),
        "Insta Payment" to "^63[7-9]\\d{13}$".toRegex(),
        "JCB" to "^(?:2131|1800|35\\d{3})\\d{11}$".toRegex(),
        "KoreanLocal" to "^9\\d{15}$".toRegex(),
        "Laser" to "^(6304|6706|6709|6771)\\d{12,15}$".toRegex(),
        "Maestro" to "^(5018|5020|5038|6304|6759|6761|6763)\\d{8,15}$".toRegex(),
        "Mastercard" to "^(5[1-5]\\d{14}|2(22[1-9]|2[3-9]\\d|[3-6]\\d\\d|7[01])\\d{12})$".toRegex(),
        "RUPAY" to "^6(?!011)(?:0\\d{14}|52[12]\\d{12})$".toRegex(),
        "Solo" to "^((6334|6767)\\d{12}|(6334|6767)\\d{14}|(6334|6767)\\d{15})$".toRegex(),
        "Switch" to "^((49(0[35]|1[16]|36)|6(333|759))\\d{12,15}|564182\\d{10,13}|633110\\d{10,13})$".toRegex(),
        "Union Pay" to "^(62\\d{14,17})$".toRegex(),
        "Visa" to "^4\\d*$".toRegex(),
        "Visa Master" to "^(?:4\\d{12}(?:\\d{3})?|5[1-5]\\d{14})$".toRegex()
    )

    // Mapping of card types to brand icons
    private val cardIcons = mapOf(
        "amex" to R.drawable.amex,
        "Visa" to R.drawable.visa,
        "Mastercard" to R.drawable.mc,
        "Rupay" to R.drawable.rupay
    )
    private lateinit var token : String
    private lateinit var amount : String
    private lateinit var currency : String
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private var isCardNumberValid = false
    private var isExpiryValid = false
    private var isCVVValid = false
    private var isCardHolderNameValid = false

    private lateinit var btnProceedToPay: Button

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

        token = arguments?.getString(TOKEN).toString()
        fetchDataListener()
        bottomSheetDialog = BottomSheetDialog(requireActivity())

        val etCardNumber: EditText = view.findViewById(R.id.etCardNumber)
        val etExpiry: EditText = view.findViewById(R.id.etExpiry)
        val tvExpiryError: TextView = view.findViewById(R.id.tvExpiryError)
        val etCardHolderName: EditText = view.findViewById(R.id.etCardHolderName)
        val etCVV: EditText = view.findViewById(R.id.etCVV)
        val tvCVVError: TextView = view.findViewById(R.id.tvCVVError)
        btnProceedToPay = view.findViewById(R.id.btnProceedToPay)
        val tvCardNumberError: TextView = view.findViewById(R.id.tvCardNumberError)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnProceedToPay.isEnabled = false
        btnProceedToPay.setBackgroundResource(R.color.colorPrimary)

        // Set up card number validation
        setupCardNumberValidation(etCardNumber, tvCardNumberError)

        // Set up expiry date validation
        setupExpiryValidation(etExpiry, tvExpiryError, etCVV)

        // Set up CVV validation
        setupCVVValidation(etCVV, tvCVVError)

        // Set up cardholder name validation
        setupCardHolderNameValidation(etCardHolderName)

        btnProceedToPay.setOnClickListener {
            // Handle payment process here
            val cardNumber = etCardNumber.text.toString().filter { !it.isWhitespace() }
            val cvv = etCVV.text.toString()
            val cardHolderName = etCardHolderName.text.toString()
            val cardExpiry = etExpiry.text.toString()
            val cardExpiryMonth = cardExpiry.split("/")[0]
            val cardExpiryYear = "20"+cardExpiry.split("/")[1]
            payAction(cardNumber, cvv, cardHolderName, cardExpiryMonth, cardExpiryYear)

        }



        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        etCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cardNumber = etCardNumber.text.toString().replace(" ", "")
                if (cardNumber.length < 12) {
                    tvCardNumberError.visibility = View.VISIBLE
                    tvCardNumberError.text = "Your card number is incomplete"
                    etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                    isCardNumberValid = false
                } else {
                    val cardType = validateCardType(cardNumber)
                    if (cardType.isNullOrEmpty()) {
                        tvCardNumberError.visibility = View.VISIBLE
                        tvCardNumberError.text = "Invalid card type"
                        etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                        isCardNumberValid = false
                    } else {
                        if (validCard(cardNumber)) {
                            tvCardNumberError.visibility = View.GONE
                            etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background)
                            isCardNumberValid = true
                            // Set the brand icon based on the card type
                            setCardBrandIcon(etCardNumber, cardType)
                        } else {
                            tvCardNumberError.visibility = View.VISIBLE
                            tvCardNumberError.text = "Invalid card number"
                            etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                            isCardNumberValid = false
                        }
                    }
                }
                updateButtonBackground() // Update the button background based on validation
            }
        }
    }

    private fun updateButtonBackground() {
        if (isCardNumberValid && isExpiryValid && isCVVValid && isCardHolderNameValid) {
            btnProceedToPay.isEnabled = true
            btnProceedToPay.setBackgroundResource(R.color.colorSecondary) // Enabled state with secondary color
        } else {
            btnProceedToPay.isEnabled = false
            btnProceedToPay.setBackgroundResource(R.color.colorPrimary) // Disabled state with primary color
        }
    }

    private fun setCardBrandIcon(etCardNumber: EditText, cardType: String) {
        val iconResId = cardIcons[cardType]
        if (iconResId != null) {
            etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(), iconResId), null)
        } else {
            // Reset to no drawable if card type doesn't match any known icons
            etCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setupCardNumberValidation(etCardNumber: EditText, tvCardNumberError: TextView) {
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                isEditing = true

                val cleanedInput = s?.toString()?.replace(Regex("\\s+"), "") ?: ""
                val formattedInput = cleanedInput.chunked(4).joinToString(" ")

                etCardNumber.setText(formattedInput)
                etCardNumber.setSelection(formattedInput.length)

                if (cleanedInput.length > 19) {
                    tvCardNumberError.text = "Your card number cannot exceed 19 digits"
                    tvCardNumberError.visibility = View.VISIBLE
                    etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                    isCardNumberValid = false
                } else {
                    tvCardNumberError.visibility = View.GONE
                    etCardNumber.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background)
                    isCardNumberValid = true
                }

                isEditing = false
                updateButtonBackground()
            }
        })
    }

    private fun setupExpiryValidation(etExpiry: EditText, tvExpiryError: TextView, etCVV: EditText) {
        etExpiry.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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

        // Focus change listener to validate and show errors
        etExpiry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val parts = etExpiry.text.toString().split("/")
                if (parts.size == 2) {
                    val month = parts[0].toIntOrNull()
                    val year = parts[1].toIntOrNull()?.plus(2000)

                    if (month == null || year == null || month !in 1..12) {
                        tvExpiryError.visibility = View.VISIBLE
                        tvExpiryError.text = "Invalid expiry date"
                        etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                        isExpiryValid = false
                    } else {
                        val calendar = Calendar.getInstance()
                        val currentYear = calendar.get(Calendar.YEAR)
                        val currentMonth = calendar.get(Calendar.MONTH) + 1

                        if (year < currentYear || (year == currentYear && month < currentMonth)) {
                            tvExpiryError.visibility = View.VISIBLE
                            tvExpiryError.text = "Card has expired"
                            etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                            isExpiryValid = false
                        } else {
                            tvExpiryError.visibility = View.GONE
                            etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background)
                            isExpiryValid = true
                        }
                    }
                } else {
                    tvExpiryError.visibility = View.VISIBLE
                    tvExpiryError.text = "Invalid expiry format"
                    etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                    isExpiryValid = false
                }

                updateButtonBackground()
            }
        }
    }

    private fun setupCVVValidation(etCVV: EditText, tvCVVError: TextView) {
        etCVV.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3)) // CVV length is limited to 3 digits
        etCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // No validation logic here, only track changes
            }
        })

        // Focus change listener to validate and show errors
        etCVV.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cvv = etCVV.text.toString()
                if (cvv.isEmpty() || cvv.length < 3) {
                    tvCVVError.visibility = View.VISIBLE
                    tvCVVError.text = "CVV must be 3 digits"
                    etCVV.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border)
                    isCVVValid = false
                } else {
                    tvCVVError.visibility = View.GONE
                    etCVV.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background)
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
                isCardHolderNameValid = !name.isNullOrEmpty()
                updateButtonBackground()
            }
        })
    }

    private fun validCard(cardNumber: String): Boolean {
        // Luhn Algorithm to validate the card number
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber.substring(i, i + 1).toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
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
                return cardType
            }
        }
        return null
    }

    private fun payAction(cardNumber: String, cvv: String, cardHolderName: String, cardExpiryMonth: String, cardExpiryYear: String) {
        val paymentMode = arrayListOf<String>()
        paymentMode.add(CREDIT_DEBIT_REF)

        val cardDataExtra = CardDataExtra(paymentMode, amount, currency)
        val cardData = CardData(cardNumber, cvv, cardHolderName, cardExpiryYear, cardExpiryMonth)
        //val cardData = CardData("4012001037141112", "233", "sss", "2028", "12")
        val processPaymentRequest = ProcessPaymentRequest(cardData, cardDataExtra)
        mainViewModel.processPayment(token, processPaymentRequest)
        mainViewModel.process_payment_response.observe(requireActivity()) { response ->
            val fetchDataResponseHandler = ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                onLoading = {
                    showProcessPaymentDialog()
                }, onSuccess = { data ->
                    bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_logo)!!.addAnimatorListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            Toast.makeText(activity, "Ended", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                            bottomSheetDialog.dismiss()
                            val i = Intent(activity, ACSPageActivity::class.java)
                            i.putExtra(REDIRECT_URL, data!!.redirect_url)
                            startActivity(i)
                            requireActivity().finish()
                        }

                    })

                }, onFailure = { errorMessage ->
                    bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_logo)!!.addAnimatorListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            Toast.makeText(activity, "Ended", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                            bottomSheetDialog.dismiss()
                            val i = Intent(requireActivity(), FailureActivity::class.java)
                            i.putExtra(ERROR_MESSAGE, errorMessage)
                            startActivity(i)
                            requireActivity().finish()
                        }

                    })
                })
            fetchDataResponseHandler.handleApiResult(response)
        }
    }

    private fun fetchDataListener() {
        mainViewModel.fetch_response.observe(requireActivity()) { response ->
            val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                onLoading = {
                }, onSuccess = { data ->
                    amount = data!!.paymentData!!.originalTxnAmount.amount
                    currency = data!!.paymentData!!.originalTxnAmount.currency
                }, onFailure = {}
            )
            fetchDataResponseHandler.handleApiResult(response)
        }
    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.process_payment_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show()
    }
}
