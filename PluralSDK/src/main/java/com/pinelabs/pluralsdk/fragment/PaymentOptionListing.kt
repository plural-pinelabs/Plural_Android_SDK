package com.pinelabs.pluralsdk.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.android.sdk.CleverTapAPI
import com.facebook.shimmer.ShimmerFrameLayout
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.PaymentOptionsAdapter
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.RecyclerViewPaymentOptionData
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils.cleverTapLog
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_NETBANKING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_UPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.PaymentModes
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel

class PaymentOptionListing : Fragment(), PaymentOptionsAdapter.OnItemClickListener {

    private lateinit var recyclerPaymentOptions: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout

    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    private lateinit var token: String
    private lateinit var TAG: String
    private var clevertapDefaultInstance: CleverTapAPI? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = arguments?.getString(TOKEN).toString()


        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.header_layout)
        activityButton.visibility = View.VISIBLE

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        cleverTapLog()

        recyclerPaymentOptions = view.findViewById(R.id.recycler_payment_options)
        shimmerLayout = view.findViewById(R.id.shimmerFrameLayout)
        startShimmer()
        mainViewModel.fetch_data_response
            .observe(viewLifecycleOwner) { response ->
                val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                    onLoading = {
                    }, onSuccess = { data ->
                        stopShimmer()
                        if (response.data != null) {
                            val paymentModes = response.data?.paymentModes?.filter { paymentMode ->
                                paymentMode.paymentModeData != null || paymentMode.paymentModeId == PAYBYPOINTS_ID
                            }
                            listData(
                                mapPaymentModes(paymentModes!!),
                                mapPaymentOptions(paymentModes!!),
                                response.data?.merchantBrandingData?.palette
                            )

                        }

                    }, onFailure = {}
                )
                fetchDataResponseHandler.handleApiResult(response)
            }
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
        return paymentModeMap
    }

    override fun onItemClick(item: RecyclerViewPaymentOptionData?) {
        //Toast.makeText(activity, item!!.payment_option, Toast.LENGTH_SHORT).show()
        loadFragment(item!!.payment_option)
    }

    fun loadFragment(paymentOption: String) {

        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        // loading which Payment Option where
        val selectedFragment = when (paymentOption) {
            PaymentModes.CREDIT_DEBIT.paymentModeName -> CardFragment()
            PaymentModes.UPI.paymentModeName -> UPICollectFragment()
            PaymentModes.NET_BANKING.paymentModeName -> NetBankingFragmentNew()
            else -> null
        }

        val TAG = when (paymentOption) {
            PaymentModes.CREDIT_DEBIT.paymentModeName -> TAG_CARD
            PaymentModes.UPI.paymentModeName -> TAG_UPI
            PaymentModes.NET_BANKING.paymentModeName -> TAG_NETBANKING
            else -> null
        }

        // Fragment Selection
        selectedFragment?.let { fragment ->

            fragment.arguments = arguments
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.details_fragment, fragment, TAG)
            transaction.addToBackStack(TAG)
            transaction.commit()
        }

        requireActivity().findViewById<LinearLayout>(R.id.saved_card_fragment).visibility = View.GONE

    }

    fun startShimmer() {
        shimmerLayout.startShimmer()
        shimmerLayout.isVisible = true
        recyclerPaymentOptions.isVisible = false
    }

    fun stopShimmer() {
        shimmerLayout.stopShimmer()
        shimmerLayout.isVisible = false
        recyclerPaymentOptions.isVisible = true
    }

    fun listData(
        paymentData: List<RecyclerViewPaymentOptionData>,
        paymentOption: List<String>,
        palette: Palette?
    ) {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val myRecyclerViewAdapter = PaymentOptionsAdapter(paymentData, paymentOption, palette, this)
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

}