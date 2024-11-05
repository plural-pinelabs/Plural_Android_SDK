package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.FailureActivity
import com.pinelabs.pluralsdk.data.model.UpiData
import com.pinelabs.pluralsdk.data.model.CardDataExtra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_REF
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import android.util.Log
import android.widget.FrameLayout

class UPICollectFragment : Fragment() {

    private lateinit var etUPIId: EditText
    private lateinit var checkCircleIcon: ImageView
    private lateinit var btnVerifyContinue: Button
    private lateinit var btnBack: ImageButton
    private lateinit var amount: String
    private lateinit var currency: String
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private lateinit var token: String

    private val UPI_REGEX = Regex("^[\\w.]{1,}-?[\\w.]{0,}-?[\\w.]{1,}@[a-zA-Z]{3,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.upicollect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        etUPIId = view.findViewById(R.id.etUPIId)
        checkCircleIcon = view.findViewById(R.id.check_circle_icon)
        btnVerifyContinue = view.findViewById(R.id.btnVerifyContinue)
        btnBack = view.findViewById(R.id.upiBtnBack)

        // Retrieve token from arguments
        token = arguments?.getString(TOKEN).toString()

        // Initialize BottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(requireActivity())

        btnVerifyContinue.setOnClickListener {
            val upiOption = etUPIId.text.toString()
            val vpa = etUPIId.text.toString()
            payAction(upiOption, vpa)
        }

        // Set up back button click listener
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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
                btnVerifyContinue.isEnabled = isValidUPI
                val color = if (isValidUPI) R.color.colorSecondary else R.color.colorPrimary
                btnVerifyContinue.setBackgroundColor(resources.getColor(color, null))
            }
        })
    }

    private fun payAction(upiOption: String, vpa: String) {
        val paymentMode = arrayListOf(UPI_REF)
        val cardDataExtra = CardDataExtra(paymentMode, amount, currency)
        val upiData = UpiData("UPI", vpa)
        val processPaymentRequest = ProcessPaymentRequest(card_data = null, cardDataExtra, upiData)

        mainViewModel.processPayment(token, processPaymentRequest)
        mainViewModel.process_payment_response.observe(viewLifecycleOwner) { response ->
            val fetchDataResponseHandler = ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                onLoading = {
                    showProcessPaymentDialog()
                }, onSuccess = {
                }, onFailure = { errorMessage ->
                    bottomSheetDialog.dismiss()
                    val intent = Intent(requireActivity(), FailureActivity::class.java)
                    intent.putExtra(ERROR_MESSAGE, errorMessage)
                    startActivity(intent)
                    requireActivity().finish()
                })
            fetchDataResponseHandler.handleApiResult(response)
        }
    }


    private fun fetchDataListener() {
        mainViewModel.fetch_response.observe(viewLifecycleOwner) { response ->
            val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                onLoading = {
                }, onSuccess = { data ->
                    amount = data!!.paymentData!!.originalTxnAmount.amount
                    currency = data.paymentData!!.originalTxnAmount.currency
                }, onFailure = {
                }
            )
            fetchDataResponseHandler.handleApiResult(response)
        }
    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.activity_timer, null)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.show() // Show the dialog first

        // Delay the fragment transaction slightly to ensure the view is available
        view.post {
            val timerContainer = view.findViewById<FrameLayout>(R.id.timer_container)
            timerContainer?.visibility = View.VISIBLE

            val timerFragment = TimerFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.timer_container, timerFragment)
                .commit()
        }
    }
}