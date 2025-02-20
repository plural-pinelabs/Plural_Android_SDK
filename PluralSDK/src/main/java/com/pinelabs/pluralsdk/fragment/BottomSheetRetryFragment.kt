package com.pinelabs.pluralsdk.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.LandingActivity
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.PaymentOptionsAdapter
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.RecyclerViewPaymentOptionData
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_NETBANKING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_PAYMENT_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.PaymentModes

class BottomSheetRetryFragment(
    amount: String,
    retryAcs: Boolean,
    paymentModes: List<PaymentMode>?,
    palette: Palette?,
    token: String?,
    errorMessage: String?
) :
    BottomSheetDialogFragment(), PaymentOptionsAdapter.OnItemClickListener {

    var paymentModesil: List<PaymentMode>? = paymentModes
    var token: String? = token
    var retryAcs = retryAcs
    var amount = amount
    var palette = palette
    var errorMessage = errorMessage

    private lateinit var txt_payment: TextView
    private lateinit var close_icon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.retry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("UPI Bottom sheet")

        txt_payment = view.findViewById(R.id.txt_payment)
        txt_payment.text = if (errorMessage?.isBlank() == true)
            context?.getString(R.string.payment_didnt) + " " + amount + " " + context?.getString(
                R.string.payment_didnt_go_through
            ) else errorMessage
        close_icon = view.findViewById(R.id.x_icon)
        close_icon.setOnClickListener {
            LandingActivity().showCancelConfirmationDialog(requireActivity(), null)
        }

        var recyclerPaymentOptions: RecyclerView = view.findViewById(R.id.recycler_payment_options)
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val myRecyclerViewAdapter = PaymentOptionsAdapter(
            mapPaymentModes(paymentModesil!!), mapPaymentOptions(
                paymentModesil!!
            ), palette, this
        )
        recyclerPaymentOptions.adapter = myRecyclerViewAdapter
        recyclerPaymentOptions.layoutManager = layoutManager
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(
            ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        )
        recyclerPaymentOptions.addItemDecoration(dividerItemDecoration)
        myRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun mapPaymentOptions(paymentModes: List<PaymentMode>): List<String> {
        val paymentOption = mutableListOf<String>()
        paymentModes.forEach { pm ->
            /*if (pm.paymentModeData == null) {*/
            paymentOption.add(pm.paymentModeId)
            /*}*/
        }
        return paymentOption
    }

    private fun mapPaymentModes(paymentModes: List<PaymentMode>): List<RecyclerViewPaymentOptionData> {
        val paymentModeMap = mutableListOf<RecyclerViewPaymentOptionData>()
        paymentModes.forEach { pm ->
            var paymentModeData = RecyclerViewPaymentOptionData()
            when (pm.paymentModeId) {
                PaymentModes.CREDIT_DEBIT.toString() -> paymentModeData =
                    RecyclerViewPaymentOptionData(
                        PaymentModes.CREDIT_DEBIT.paymentModeImage,
                        PaymentModes.CREDIT_DEBIT.paymentModeName
                    )

                PaymentModes.NET_BANKING.toString() -> paymentModeData =
                    RecyclerViewPaymentOptionData(
                        PaymentModes.NET_BANKING.paymentModeImage,
                        PaymentModes.NET_BANKING.paymentModeName
                    )

                PaymentModes.UPI.toString() -> paymentModeData = RecyclerViewPaymentOptionData(
                    PaymentModes.UPI.paymentModeImage, PaymentModes.UPI.paymentModeName
                )
            }
            if (!paymentModeData.payment_option.isEmpty())
                paymentModeMap.add(paymentModeData)
        }
        /*if (paymentModeMap.size > 5) {*/
        return paymentModeMap/*.subList(0, 2)*/.also {
            it.add(
                RecyclerViewPaymentOptionData(
                    PaymentModes.ALL_PAYMENT.paymentModeImage,
                    PaymentModes.ALL_PAYMENT.paymentModeName
                )
            )
        }
        /*} else return paymentModeMap*/

    }

    override fun onItemClick(item: RecyclerViewPaymentOptionData?) {
        this.dismiss()
        /*val i = Intent(requireActivity(), LandingActivity::class.java)
        i.putExtra(TOKEN, token)
        startActivity(i)
        requireActivity().finish()*/
        loadFragment(item?.payment_option)
    }

    fun loadFragment(paymentOption: String?) {

        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        // loading which Payment Option where
        val selectedFragment = when (paymentOption) {
            PaymentModes.CREDIT_DEBIT.paymentModeName -> CardFragment()
            PaymentModes.UPI.paymentModeName -> UPICollectFragment()
            PaymentModes.NET_BANKING.paymentModeName -> NetBankingFragment()
            else -> PaymentOptionListing()
        }

        val TAG = when (paymentOption) {
            PaymentModes.CREDIT_DEBIT.paymentModeName -> TAG_CARD
            PaymentModes.UPI.paymentModeName -> TAG_UPI
            PaymentModes.NET_BANKING.paymentModeName -> TAG_NETBANKING
            else -> TAG_PAYMENT_LISTING
        }

        // Fragment Selection
        selectedFragment?.let { fragment ->
            fragment.arguments = arguments
            requireActivity().supportFragmentManager.popBackStack()
            if (retryAcs)
                showPaymentListingFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.details_fragment, fragment, TAG)
            transaction.addToBackStack(TAG)
            transaction.commit()
        }
    }

    fun showPaymentListingFragment() {
        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        val paymentListingFragment = PaymentOptionListing()
        paymentListingFragment.arguments = arguments

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, paymentListingFragment, TAG_PAYMENT_LISTING)
        transaction.commit()
    }

}