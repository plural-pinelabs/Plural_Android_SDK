package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.SavedCardAdapter
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.SavedCardData
import com.pinelabs.pluralsdk.utils.Constants.Companion.CUSTOMER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.MOBILE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PROCESS_PAYMENT_REQUEST
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import com.pinelabs.pluralsdk.viewmodels.SavedCardViewModel

class BottomSheetSavedCard(savedCardChecked: Boolean) : BottomSheetDialogFragment() {

    private lateinit var recycler_savedCard: RecyclerView
    private var saveCardList = mutableListOf<SavedCardData>()

    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    private val savedCardViewModel by activityViewModels<SavedCardViewModel>()

    private lateinit var btnSavedCardPay: Button
    private lateinit var btnWithoutPaying: Button
    private lateinit var btnOk: Button
    private lateinit var imgClose: ImageView

    private var savedCardChecked: Boolean = savedCardChecked
    private var customerId: String? = null
    private var mobileNumber: String? = null
    private var token: String? = null
    private var processPaymentRequest: ProcessPaymentRequest? = null
    private var isMobileNumberValidated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_card_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getString(CUSTOMER_ID)
        mobileNumber = arguments?.getString(MOBILE)
        token = arguments?.getString(TOKEN)
        processPaymentRequest =
            arguments?.getSerializable(PROCESS_PAYMENT_REQUEST) as ProcessPaymentRequest?

        btnSavedCardPay = view.findViewById(R.id.btnSavedCardPay)
        btnWithoutPaying = view.findViewById(R.id.btnWithoutPaying)
        btnOk = view.findViewById(R.id.btnOk)
        imgClose = view.findViewById(R.id.x_icon)
        imgClose.setOnClickListener {
            this@BottomSheetSavedCard.dismiss()
        }

        btnSavedCardPay.background = buttonBackground(requireActivity())
        btnSavedCardPay.isEnabled = true

        btnWithoutPaying.isEnabled = true

        btnOk.background = buttonBackground(requireActivity())
        btnOk.isEnabled = true

        mainViewModel.mobileNumberValidate.observe(viewLifecycleOwner) { isValidated ->
            this.isMobileNumberValidated = isValidated
        }

        btnSavedCardPay.setOnClickListener {
            if (isMobileNumberValidated) {
                token?.let { it1 ->
                    this@BottomSheetSavedCard.dismiss()
                    mainViewModel.processPayment(it1, processPaymentRequest!!)
                }
            } else {
                val otpRequest = OTPRequest(null, null, customerId, null, null)
                savedCardViewModel.sendOTPCustomer(token, otpRequest)
            }

        }

        btnWithoutPaying.setOnClickListener {
            token?.let { it1 ->
                this@BottomSheetSavedCard.dismiss()
                processPaymentRequest?.card_data?.save = null
                mainViewModel.processPayment(it1, processPaymentRequest!!)
            }
        }

        btnOk.setOnClickListener {
            if (isMobileNumberValidated) {
                token?.let { it1 ->
                    this@BottomSheetSavedCard.dismiss()
                    mainViewModel.processPayment(it1, processPaymentRequest!!)
                }
            } else {
                val otpRequest = OTPRequest(null, null, customerId, null, null)
                savedCardViewModel.sendOTPCustomer(token, otpRequest)
            }

        }

        if (!savedCardChecked) {
            btnOk.visibility = View.GONE
            btnSavedCardPay.visibility = View.VISIBLE
            btnWithoutPaying.visibility = View.VISIBLE
        } else {
            btnOk.visibility = View.VISIBLE
            btnSavedCardPay.visibility = View.GONE
            btnWithoutPaying.visibility = View.GONE
        }

        recycler_savedCard = view.findViewById(R.id.saved_card_item)

        saveCardList.add(
            SavedCardData(
                R.drawable.saved_card_secure,
                getString(R.string.secure_transaction)
            )
        )
        saveCardList.add(
            SavedCardData(
                R.drawable.save_card_quicker,
                getString(R.string.quicker_transaction)
            )
        )
        saveCardList.add(
            SavedCardData(
                R.drawable.saved_card_everything,
                getString(R.string.everything_transaction)
            )
        )

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_savedCard.layoutManager = layoutManager
        val savedCardAdapter = SavedCardAdapter(saveCardList)
        recycler_savedCard.adapter = savedCardAdapter
        savedCardAdapter.notifyDataSetChanged()
    }

    fun buttonBackground(context: Context): Drawable {

        val stateListDrawable = StateListDrawable()

        // Create different drawables for different states
        val pressedDrawable = GradientDrawable().apply {
            /*if (palette != null) {
                setColor(Color.parseColor(palette?.C900))
            } else {*/
            setColor(context.resources.getColor(R.color.header_color))
            /*}*/
            cornerRadius = 16f // Normal corner radius
        }

        // Add states to the StateListDrawable
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), pressedDrawable) // Default state

        return stateListDrawable
    }

}