package com.pinelabs.pluralsdk.fragment

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.ACSPageActivity
import com.pinelabs.pluralsdk.data.model.CardData
import com.pinelabs.pluralsdk.data.model.CardDataExtra
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel

class CardFragment : Fragment() {

    private lateinit var token : String

    private val mainViewModel by activityViewModels<FetchDataViewModel>()

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

        val etCardNumber: EditText = view.findViewById(R.id.etCardNumber)
        val etExpiry: EditText = view.findViewById(R.id.etExpiry)
        val tvExpiryError: TextView = view.findViewById(R.id.tvExpiryError)
        val etCardHolderName: EditText = view.findViewById(R.id.etCardHolderName)
        val etCVV: EditText = view.findViewById(R.id.etCVV)
        val tvCVVError: TextView = view.findViewById(R.id.tvCVVError)
        val btnProceedToPay: Button = view.findViewById(R.id.btnProceedToPay)
        val tvCardNumberError: TextView = view.findViewById(R.id.tvCardNumberError)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        // Set up card number validation
        setupCardNumberValidation(etCardNumber, tvCardNumberError)

        // Set up expiry date validation
        setupExpiryValidation(etExpiry, tvExpiryError)

        // Set up CVV validation with focus shifting to card holder name
        setupCVVValidation(etCVV, tvCVVError, etCardHolderName, btnProceedToPay)

        // Set up InputFilter for Card Holder's Name
        setupCardHolderNameValidation(etCardHolderName)

        btnProceedToPay.setOnClickListener {
            // Handle payment process here
            val cardNumber = etCardNumber.text.toString()
            val cvv = etCVV.text.toString()
            val cardHolderName = etCardHolderName.text.toString()
            val cardExpiry = etExpiry.text.toString()
            val cardExpiryMonth = cardExpiry.split("/")[0]
            val cardExpiryYear = cardExpiry.split("/")[1]
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
                } else {
                    val cardType = validateCardType(cardNumber)
                    if (cardType.isNullOrEmpty()) {
                        tvCardNumberError.visibility = View.VISIBLE
                        tvCardNumberError.text = "Invalid card type"
                    } else {
                        if (validCard(cardNumber)) {
                            tvCardNumberError.visibility = View.GONE
                        } else {
                            tvCardNumberError.visibility = View.VISIBLE
                            tvCardNumberError.text = "Invalid card number"
                        }
                    }
                }
            } else {
                tvCardNumberError.visibility = View.GONE
            }
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
                } else {
                    tvCardNumberError.visibility = View.GONE
                    val cardType = validateCardType(cleanedInput)
                    if (cardType.isNullOrEmpty()) {
                        tvCardNumberError.text = "Invalid card type"
                        tvCardNumberError.visibility = View.VISIBLE
                    }
                }

                isEditing = false
            }
        })

        etCardNumber.filters = arrayOf(
            InputFilter.LengthFilter(23),
            InputFilter { source, start, end, dest, dstart, dend ->
                if (source.toString().contains("\n")) {
                    return@InputFilter ""
                }
                null
            }
        )
    }

    private fun validateCardType(cardNumber: String): String? {
        for ((type, regex) in cardTypes) {
            if (regex.matches(cardNumber)) {
                return type
            }
        }
        return null
    }

    private fun setupExpiryValidation(etExpiry: EditText, tvExpiryError: TextView) {
        // Initially hide the error message
        tvExpiryError.visibility = View.GONE

        // Format the input while typing
        etExpiry.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return // Prevent recursion

                isEditing = true
                val input = s.toString().replace(Regex("[^\\d]"), "") // Remove non-digit characters

                // Format the input to MM/YY
                val formattedInput = when {
                    input.length <= 2 -> input
                    input.length <= 4 -> "${input.substring(0, 2)}/${input.substring(2)}"
                    else -> "${input.substring(0, 2)}/${input.substring(2, 4)}"
                }

                etExpiry.setText(formattedInput)
                etExpiry.setSelection(formattedInput.length) // Move the cursor to the end

                isEditing = false
            }
        })

        // Validate expiry when losing focus
        etExpiry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = etExpiry.text.toString().replace(Regex("[^\\d]"), "")
                // Check for a valid date only if 4 digits are entered
                if (input.length == 4) {
                    val month = input.substring(0, 2).toIntOrNull() ?: 0
                    val year = input.substring(2, 4).toIntOrNull() ?: 0

                    // Validate the month and year
                    if (month !in 1..12 || year < 24) {
                        etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_border) // Set error background
                        tvExpiryError.text = "Not a valid date" // Show error message
                        tvExpiryError.visibility = View.VISIBLE // Show error text
                    } else {
                        etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background) // Reset background
                        tvExpiryError.visibility = View.GONE // Hide error message
                    }
                } else {
                    // Reset the background if the length is not 4 yet
                    etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background) // Reset background
                    tvExpiryError.visibility = View.GONE // Hide error message
                }
            } else {
                // Reset the background and error message when the field gains focus
                etExpiry.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_custom_background) // Reset background
                tvExpiryError.visibility = View.GONE // Hide error message
            }
        }
    }

    private fun setupCVVValidation(etCVV: EditText, tvCVVError: TextView, etCardHolderName: EditText, btnProceedToPay: Button) {
        etCVV.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cvv = etCVV.text.toString()
                if (cvv.length < 3 || cvv.length > 4) {
                    tvCVVError.visibility = View.VISIBLE
                    tvCVVError.text = "Not a valid CVV"
                } else {
                    tvCVVError.visibility = View.GONE
                }
            } else {
                tvCVVError.visibility = View.GONE
            }
        }
    }

    private fun setupCardHolderNameValidation(etCardHolderName: EditText) {
        etCardHolderName.filters = arrayOf(InputFilter.LengthFilter(50)) // Set max length
    }

    private fun validCard(cardNumber: String): Boolean {
        // Implement Luhn's algorithm for card validation
        val digits = cardNumber.map { it.toString().toInt() }
        val sum = digits.reversed().mapIndexed { index, digit ->
            if (index % 2 == 1) {
                val doubled = digit * 2
                if (doubled > 9) doubled - 9 else doubled
            } else {
                digit
            }
        }.sum()
        return sum % 10 == 0
    }

    private fun payAction(cardNumber: String, cvv: String, cardHolderName: String, cardExpiryMonth: String, cardExpiryYear: String) {
        val paymentMode = arrayListOf<String>()
        paymentMode.add("CREDIT_DEBIT")

        val cardDataExtra = CardDataExtra(paymentMode, "50000", "INR")
        //val cardData = CardData(cardNumber, cvv, cardHolderName, cardExpiryYear, cardExpiryMonth)
        val cardData = CardData("4012001037141112", "233", "sss", "2028", "12")
        val processPaymentRequest = ProcessPaymentRequest(cardData, cardDataExtra)
        mainViewModel.processPayment(token, processPaymentRequest)
        mainViewModel.process_payment_response.observe(requireActivity()) { response ->
            val fetchDataResponseHandler = ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                onLoading = {
                }, onSuccess = { data ->
                    Toast.makeText(activity,data!!.redirect_url, Toast.LENGTH_SHORT).show()
                    val i = Intent(activity, ACSPageActivity::class.java)
                    i.putExtra(REDIRECT_URL, data!!.redirect_url)
                    startActivity(i)
                    requireActivity().finish()
                }, onFailure = {

                })
            fetchDataResponseHandler.handleApiResult(response)
        }
    }
}
