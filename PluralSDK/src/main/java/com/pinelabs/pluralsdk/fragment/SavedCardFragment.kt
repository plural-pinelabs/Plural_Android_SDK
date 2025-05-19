package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.SavedCardListAdapter
import com.pinelabs.pluralsdk.data.model.CardTokenData
import com.pinelabs.pluralsdk.data.model.CustomerData
import com.pinelabs.pluralsdk.data.model.CustomerInfoResponse
import com.pinelabs.pluralsdk.data.model.Extra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.SavedCardTokens
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.fragment.CardFragment.onRetryListener
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_CARD
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_SAVED_CARD_LISTING
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel

class SavedCardFragment(isLanding: Boolean, mobile: String?, email: String?, token: String) :
    Fragment(),
    SavedCardListAdapter.OnItemClickListener {

    private lateinit var recyclerSavedCard: RecyclerView
    private lateinit var backButton: ImageButton
    private var palette: Palette? = null

    private var isLanding = isLanding
    private var isAddCard: Boolean = false

    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    private var amount: Int? = null
    private var currency: String? = null
    private var email: String? = email
    private var mobile: String? = mobile
    private var token: String = token

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var buttonClicked: Boolean = false

    private var listener: onRetryListener? = null

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
        return inflater.inflate(R.layout.saved_card_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetDialog = BottomSheetDialog(requireActivity())

        backButton = view.findViewById(R.id.btnBack)

        if (!isLanding)
            backButton.visibility = View.VISIBLE
        else
            backButton.visibility = View.GONE

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        mainViewModel.fetch_data_response.observe(viewLifecycleOwner) { response ->
            val fetchResponse =
                ApiResultHandler<FetchResponse>(
                    requireActivity(),
                    onLoading = {},
                    onSuccess = { fetchResponse ->

                        amount = fetchResponse?.paymentData?.originalTxnAmount?.amount
                        currency = fetchResponse?.paymentData?.originalTxnAmount?.currency

                        fetchResponse?.merchantBrandingData?.palette?.let { palette ->
                            this.palette = palette
                        }

                        fetchResponse?.customerInfo?.tokens?.let { data ->
                            populateData(data)
                        }
                    },
                    onFailure = {})
            fetchResponse.handleApiResult(response)
        }

        mainViewModel.saved_card_validate_update_order_response.observe(viewLifecycleOwner) { response ->
            val responseHandler =
                ApiResultHandler<CustomerInfoResponse>(requireActivity(), onLoading = {
                    Utils.println("Update customer loading")
                }, onSuccess = { data ->
                    Utils.println("Update customer info ${data?.customerInfo?.tokens?.size}")
                    data?.customerInfo?.tokens?.let { data ->
                        populateData(data)
                    }
                }, onFailure = { errorMessage ->
                    Utils.println("Update customer info ${errorMessage?.error_message}")
                })
            responseHandler.handleApiResult(response)
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
                                mainViewModel.paymentId.value = data?.payment_id

                                bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_logo)!!
                                    .addAnimatorListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(p0: Animator) {
                                        }

                                        override fun onAnimationEnd(p0: Animator) {
                                        }

                                        override fun onAnimationCancel(p0: Animator) {
                                        }

                                        override fun onAnimationRepeat(p0: Animator) {
                                            bottomSheetDialog.dismiss()
                                            redirectToACS(
                                                startTime,
                                                data?.redirect_url,
                                                data?.order_id,
                                                data?.payment_id
                                            )

                                        }

                                    })

                            }, onFailure = { errorMessage ->
                                bottomSheetDialog.dismiss()

                                listener?.onRetry(
                                    false,
                                    errorMessage?.error_code,
                                    errorMessage?.error_message
                                )
                            })
                    fetchDataResponseHandler.handleApiResult(response)
                }
            }

        recyclerSavedCard = view.findViewById(R.id.recycler_saved_cards)

    }

    override fun OnPaymentClick(item: SavedCardTokens?, cvv: String?) {
        buttonClicked = true
        val cardTokenData = CardTokenData(item?.tokenId, cvv)
        val customerData = CustomerData(mobile, email)
        val paymentMode = arrayListOf<String>()
        paymentMode.add(CREDIT_DEBIT_ID)
        val cardDataExtra =
            Extra(
                paymentMode,
                amount,
                currency,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Utils.createSDKData(requireActivity())
            )
        val processPaymentRequest =
            ProcessPaymentRequest(
                cardTokenData,
                customerData,
                null,
                null,
                null,
                null,
                cardDataExtra,
                null,
                null,
            )
        Utils.println("Process payment request ${Gson().toJson(processPaymentRequest)}")
        mainViewModel.processPayment(
            token,
            processPaymentRequest
        )

    }

    override fun onAddCard() {
        this.requireActivity()
            .findViewById<LinearLayout>(R.id.saved_card_fragment).visibility =
            View.GONE

        val fragment = CardFragment()
        val arguments = Bundle()
        arguments.putString(TOKEN, token)

        fragment.arguments = arguments

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, fragment, TAG_CARD)
        transaction.addToBackStack(TAG_CARD)
        transaction.commit()
    }

    override fun onViewAllCards() {
        this.requireActivity()
            .findViewById<LinearLayout>(R.id.saved_card_fragment).visibility =
            View.GONE
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.details_fragment,
            SavedCardFragment(false, email, mobile, token),
            TAG_SAVED_CARD_LISTING
        )
        transaction.addToBackStack(TAG_SAVED_CARD_LISTING)
        transaction.commit()
    }

    fun populateData(tokenList: List<SavedCardTokens>) {

        if (!isLanding || (isLanding && tokenList.size < 2))
            isAddCard = true

        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val myRecyclerViewAdapter = SavedCardListAdapter(
            requireActivity(),
            if (isLanding && tokenList.size > 2)
                tokenList?.subList(0, 2)
            else
                tokenList,
            this@SavedCardFragment,
            palette,
            isAddCard,
            amount
        )
        recyclerSavedCard.adapter = myRecyclerViewAdapter
        recyclerSavedCard.layoutManager = layoutManager
        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )
        recyclerSavedCard.addItemDecoration(dividerItemDecoration)
        myRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun showProcessPaymentDialog() {
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.process_payment_bottom_sheet, null)

        var logoAnimation: LottieAnimationView = view.findViewById(R.id.img_process_logo)
        logoAnimation.setAnimationFromUrl(IMAGE_LOGO)

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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

}