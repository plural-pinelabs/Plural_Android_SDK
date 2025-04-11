package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.LandingActivity
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RiskValidationDetails
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.utils.Constants.Companion.AMOUNT
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PALETTE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PROCESS_PAYMENT_REQUEST
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel


class AddressFragment : Fragment() {

    var etFirstName: EditText? = null
    var etLastName: EditText? = null
    var etEmail: EditText? = null
    var etAddress1: EditText? = null
    var etAddress2: EditText? = null
    var etCity: EditText? = null
    var etZipCode: EditText? = null
    var etState: EditText? = null
    var etCountry: EditText? = null
    var btnProceedToPay: Button? = null

    var processPaymentRequest: ProcessPaymentRequest? = null
    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    var token: String? = null
    var amount: String? = null

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var buttonClicked: Boolean = false

    private var paymentId: String? = null
    private var palette: Palette? = null
    private var startTime: Long? = null
    private var redirectUrl: String? = null
    private var listener: AddressFragment.onRetryListener? = null

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
        return inflater.inflate(R.layout.dcc_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetDialog = BottomSheetDialog(requireActivity())

        processPaymentRequest =
            arguments?.getParcelable(PROCESS_PAYMENT_REQUEST)
        token = arguments?.getString(TOKEN)
        amount = arguments?.getString(AMOUNT)
        palette = arguments?.getParcelable(PALETTE)

        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etEmail = view.findViewById(R.id.etEmail)
        etAddress1 = view.findViewById(R.id.etAddress)
        etAddress2 = view.findViewById(R.id.etAddressOptional)
        etCity = view.findViewById(R.id.etCity)
        etZipCode = view.findViewById(R.id.etZipcode)
        etState = view.findViewById(R.id.etState)
        etCountry = view.findViewById(R.id.etCountry)
        btnProceedToPay = view.findViewById(R.id.btnProceedToPay)
        btnProceedToPay?.text = amount

        btnProceedToPay?.background = buttonBackground(requireActivity(), palette)
        btnProceedToPay?.isEnabled = false
        btnProceedToPay?.alpha = 0.3f

        btnProceedToPay?.setOnClickListener {

            buttonClicked = true

            val riskValidationRequest = RiskValidationDetails(
                etFirstName?.text.toString(),
                etLastName?.text.toString(),
                etEmail?.text.toString(),
                etAddress1?.text.toString(),
                etAddress2?.text.toString(),
                etCity?.text.toString(),
                etState?.text.toString(),
                etCountry?.text.toString(),
                etZipCode?.text.toString()
            )

            processPaymentRequest?.extras?.risk_validation_details = riskValidationRequest
            mainViewModel.processPayment(token, processPaymentRequest)

        }

        mainViewModel.process_payment_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                if (buttonClicked) {
                    startTime = System.currentTimeMillis()
                    val fetchDataResponseHandler =
                        ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                            onLoading = {
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
                                            bottomSheetDialog?.dismiss()

                                            redirectToACS(
                                                startTime,
                                                data?.redirect_url,
                                                data?.order_id,
                                                data?.payment_id
                                            )

                                        }

                                    })

                            }, onFailure = { errorMessage ->

                                bottomSheetDialog?.dismiss()

                                listener?.onRetry(
                                    false,
                                    errorMessage?.error_code,
                                    errorMessage?.error_message
                                )

                            })
                    fetchDataResponseHandler.handleApiResult(response)

                }
            }

        addTextWatchers()

    }

    fun buttonBackground(context: Context, palette: Palette?): Drawable {

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


    private fun addTextWatchers() {
        etFirstName?.addTextChangedListener(GenericTextWatcher(etFirstName))
        etLastName?.addTextChangedListener(GenericTextWatcher(etLastName))
        etEmail?.addTextChangedListener(GenericTextWatcher(etEmail))
        etAddress1?.addTextChangedListener(GenericTextWatcher(etAddress1))
        etCity?.addTextChangedListener(GenericTextWatcher(etCity))
        etZipCode?.addTextChangedListener(GenericTextWatcher(etZipCode))
        etState?.addTextChangedListener(GenericTextWatcher(etState))
        etCountry?.addTextChangedListener(GenericTextWatcher(etCountry))
    }

    fun validateForm(): Boolean {
        // Validate all form fields
        if (etFirstName?.getText().toString().isEmpty() ||
            etLastName?.getText().toString().isEmpty() ||
            etEmail?.getText().toString().isEmpty() ||
            etAddress1?.getText().toString().isEmpty() ||
            etCity?.getText().toString().isEmpty() ||
            etZipCode?.getText().toString().isEmpty() ||
            etState?.getText().toString().isEmpty() ||
            etCountry?.getText().toString().isEmpty() ||
            !Utils.isValidName(etFirstName?.text.toString()) ||
            !Utils.isValidName(etLastName?.text.toString()) ||
            !Utils.isValidEmail(etEmail?.text.toString()) ||
            !Utils.isValidPincode(etZipCode?.text.toString())
        ) {
            return false
        }
        return true
    }

    inner class GenericTextWatcher(private var editText: EditText?) : TextWatcher {

        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            after: Int
        ) {
            if (validateForm()) {
                btnProceedToPay?.setEnabled(true)
                btnProceedToPay?.alpha = 1f
            } else {
                btnProceedToPay?.setEnabled(false)
                btnProceedToPay?.alpha = .3f
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

}