package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.utils.Utils.buttonBackground
import com.pinelabs.pluralsdk.data.utils.Utils.isValidEmail
import com.pinelabs.pluralsdk.data.utils.Utils.isValidPhoneNumber
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_DETAILS
import com.pinelabs.pluralsdk.utils.Constants.Companion.EMAIL
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel

class BottomSheetMobileNumber(palette: Palette?) : BottomSheetDialogFragment() {

    private lateinit var btnSavedCardPay: Button
    private lateinit var edtMobileNumber: EditText
    private lateinit var edtEmail: EditText
    private lateinit var txtMobileError: TextView
    private lateinit var txtEmailError: TextView
    private lateinit var relativeMobileNumber: RelativeLayout
    private lateinit var imgClose: ImageView
    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    private var customerInfo: CustomerInfo? = null
    private var token: String? = null
    private var customerId: String? = null

    private var email: String? = null
    private var mobile: String? = null

    private var palette: Palette? = palette

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerInfo = arguments?.getParcelable<CustomerInfo>(CUSTOMER_DETAILS)
        token = arguments?.getString(TOKEN)

        mobile =
            if (customerInfo?.mobileNo != null) customerInfo?.mobileNo else arguments?.getString(
                MOBILE
            )
        email =
            if (customerInfo?.emailId != null) customerInfo?.emailId else arguments?.getString(EMAIL)

        relativeMobileNumber = view.findViewById(R.id.layout_mobile_number)

        btnSavedCardPay = view.findViewById(R.id.btnProceedToPay)
        btnSavedCardPay.background = buttonBackground(requireActivity(), palette)
        btnSavedCardPay.isEnabled = false
        btnSavedCardPay.alpha = 0.3f

        edtMobileNumber = view.findViewById(R.id.edt_mobile_number)
        edtMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotEmpty()) {
                    relativeMobileNumber.setBackgroundResource(R.drawable.edittext_default_border)
                    edtEmail.setBackgroundResource(R.drawable.edittext_default_border)
                    txtMobileError.visibility = View.GONE
                }
                if (charSequence.length == 10) {
                    btnSavedCardPay.isEnabled = true
                    btnSavedCardPay.alpha = 1f
                } else {
                    btnSavedCardPay.isEnabled = false
                    btnSavedCardPay.alpha = 0.3f
                }
            }

            override fun afterTextChanged(editable: Editable) {
                relativeMobileNumber.background = setColor(requireActivity())
            }
        })

        edtEmail = view.findViewById(R.id.edt_email)
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotEmpty()) {
                    relativeMobileNumber.setBackgroundResource(R.drawable.edittext_default_border)
                    edtEmail.setBackgroundResource(R.drawable.edittext_default_border)
                    txtEmailError.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {
                edtEmail.background = setColor(requireActivity())
            }
        })

        txtMobileError = view.findViewById(R.id.tvMobileNumberError)
        txtEmailError = view.findViewById(R.id.tvEmailError)

        edtMobileNumber.setText(mobile)
        edtEmail.setText(email)
        relativeMobileNumber.setBackgroundResource(R.drawable.edittext_default_border)
        edtEmail.setBackgroundResource(R.drawable.edittext_default_border)

        imgClose = view.findViewById(R.id.x_icon)

        imgClose.setOnClickListener {
            this@BottomSheetMobileNumber.dismiss()
        }

        btnSavedCardPay.setOnClickListener {
            if (edtMobileNumber.text.isEmpty() || !isValidPhoneNumber(
                    edtMobileNumber.text.toString()
                )
            ) {
                txtMobileError.visibility = View.VISIBLE
                relativeMobileNumber.setBackgroundResource(R.drawable.edittext_error_border)
            } else if (edtEmail.text.isNotEmpty() && !isValidEmail(edtEmail.text.toString())) {
                txtEmailError.visibility = View.VISIBLE
                edtEmail.setBackgroundResource(R.drawable.edittext_error_border)
            } else {
                customerInfo?.mobileNumber = edtMobileNumber.text.toString()
                customerInfo?.countryCode = "91"
                customerInfo?.emailId = edtEmail.text.toString()
                customerInfo?.is_edit = false
                mainViewModel.createInactive(token, customerInfo)
            }
            /*val otpRequest = OTPRequest(null, null, "cust-v1-250211051430-aa-EhHAv8", null, null)
            mainViewModel.sendOTPCustomer(token, otpRequest)*/
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

}