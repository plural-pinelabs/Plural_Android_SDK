package com.pinelabs.pluralsdk.fragment

import android.animation.Animator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.clevertap.android.sdk.CleverTapAPI
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.GridDividerItemDecoration
import com.pinelabs.pluralsdk.adapter.NetBankAllAdapterNew
import com.pinelabs.pluralsdk.adapter.NetBanksAdapterNew
import com.pinelabs.pluralsdk.adapter.WalletAdapter
import com.pinelabs.pluralsdk.adapter.WalletAllAdapter
import com.pinelabs.pluralsdk.adapter.loadSvgOrOther
import com.pinelabs.pluralsdk.data.model.AcquirerWisePaymentData
import com.pinelabs.pluralsdk.data.model.DeviceInfo
import com.pinelabs.pluralsdk.data.model.Extra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.NetBank
import com.pinelabs.pluralsdk.data.model.NetBankingData
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentMode
import com.pinelabs.pluralsdk.data.model.PaymentOption
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.Wallet
import com.pinelabs.pluralsdk.data.model.WalletBank
import com.pinelabs.pluralsdk.data.model.WalletData
import com.pinelabs.pluralsdk.data.model.issuerDataList
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.data.utils.Utils.cleverTapLog
import com.pinelabs.pluralsdk.utils.CleverTapUtil
import com.pinelabs.pluralsdk.utils.Constants.Companion.CT_NETBANKING
import com.pinelabs.pluralsdk.utils.Constants.Companion.IMAGE_LOGO
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_PAYMENT_METHOD
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_INITIATED
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.START_TIME
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ACS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.Constants.Companion.WALLET_PAYMENT_METHOD
import com.pinelabs.pluralsdk.utils.DeviceType
import com.pinelabs.pluralsdk.utils.PaymentModes
import com.pinelabs.pluralsdk.utils.TransactionMode
import com.pinelabs.pluralsdk.utils.WALLET
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import java.lang.reflect.Type
import java.util.EnumSet


class WalletFragment : Fragment(), WalletAllAdapter.OnItemClickListener {

    private lateinit var imgBack: ImageButton
    private lateinit var flexNetBanks: FlexboxLayout
    private lateinit var recyclerNetBanks: RecyclerView
    private lateinit var linearMoreBanks: LinearLayout
    private lateinit var moreBankAdapter: WalletAllAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var moreBanksBottomSheetDialog: BottomSheetDialog

    private lateinit var token: String
    private var amount: Int? = 0
    private lateinit var currency: String
    private var bankList: List<WalletBank?> = mutableListOf()

    private var palette: Palette? = null
    private var orderId: String? = null

    private var clevertapDefaultInstance: CleverTapAPI? = null

    private val mainViewModel by activityViewModels<FetchDataViewModel>()
    var paymentModes: List<PaymentMode>? = mutableListOf()

    var buttonClicked: Boolean = false
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wallet_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClicked = false

        token = arguments?.getString(TOKEN).toString()

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        cleverTapLog()

        bottomSheetDialog = BottomSheetDialog(requireActivity())
        moreBanksBottomSheetDialog = BottomSheetDialog(requireActivity())

        val activityButton = requireActivity().findViewById<ConstraintLayout>(R.id.header_layout)
        activityButton.visibility = View.VISIBLE

        //Backpress
        imgBack = view.findViewById(R.id.btnBack)
        imgBack.setOnClickListener {
            //clevertapDefaultInstance?.let { CT_EVENT_PAYMENT_CANCELLED(it, false, true) }
            //requireActivity().supportFragmentManager.popBackStack()
            requireActivity().onBackPressed()
        }

        flexNetBanks = view.findViewById(R.id.flex_wallet_banks_grid)
        //Setting Banks in Grid
        recyclerNetBanks = view.findViewById(R.id.recycler_wallet_banks_grid)
        setNetBankingGrid()


        //More bank button
        linearMoreBanks = view.findViewById(R.id.linear_more_wallets)
        linearMoreBanks.setOnClickListener {
            showMoreBanks()
        }

        setLiveDataListener()
        mainViewModel.process_payment_response.observe(viewLifecycleOwner) { response ->
            if (buttonClicked) {
                val startTime = System.currentTimeMillis()
                val fetchDataResponseHandler =
                    ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                        onLoading = {
                            if (moreBanksBottomSheetDialog != null && moreBanksBottomSheetDialog.isShowing) moreBanksBottomSheetDialog.dismiss()
                            showProcessPaymentDialog()
                        }, onSuccess = { response ->

                            bottomSheetDialog.findViewById<LottieAnimationView>(R.id.img_process_logo)!!
                                .addAnimatorListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {

                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                    }

                                    override fun onAnimationCancel(animation: Animator) {
                                    }

                                    override fun onAnimationRepeat(animation: Animator) {

                                        bottomSheetDialog.dismiss()
                                        //LandingActivity().paymentId = response?.payment_id
                                        mainViewModel.paymentId.value = response?.payment_id

                                        val arguments = Bundle()
                                        arguments.putString(TOKEN, token)
                                        arguments.putLong(START_TIME, startTime)
                                        arguments.putString(
                                            REDIRECT_URL,
                                            response!!.redirect_url
                                        )
                                        arguments.putString(ORDER_ID, response?.order_id)
                                        arguments.putString(PAYMENT_ID, response?.payment_id)
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

                                })

                            /*val i = Intent(activity, ACSPageActivity::class.java)
                            i.putExtra(TOKEN, token)
                            i.putExtra(REDIRECT_URL, response!!.redirect_url)
                            i.putExtra(ORDER_ID, response?.order_id)
                            i.putExtra(PAYMENT_ID, response?.payment_id)
                            startActivity(i)
                            requireActivity().finish()*/

                        }, onFailure = { errorMessage ->
                            bottomSheetDialog.dismiss()
                            Utils.println("Process payment : Netbanking: Failure")
                            /*val intent = Intent(requireActivity(), FailureActivity::class.java)
                        intent.putExtra(ORDER_ID, orderId)
                        intent.putExtra(ERROR_CODE, errorMessage?.error_code)
                        intent.putExtra(ERROR_MESSAGE, errorMessage?.error_message)
                        startActivity(intent)
                        requireActivity().finish()*/
                            listener?.onRetry(
                                false,
                                errorMessage?.error_code,
                                errorMessage?.error_message
                            )
                        })
                fetchDataResponseHandler.handleApiResult(response)
            }
        }

    }

    override fun onItemClick(item: WalletBank?) {
        buttonClicked = true
        val bankCode = item?.bankCode
        val processPaymentRequest =
            createProcessPaymentRequest(item?.bankName, bankCode, amount!!, currency)
        mainViewModel.processPayment(token, processPaymentRequest)
    }

    private fun createProcessPaymentRequest(
        bankName: String?,
        payCode: String?,
        amount: Int,
        currency: String
    ): ProcessPaymentRequest {

        CleverTapUtil.CT_EVENT_PAYMENT_METHOD(
            clevertapDefaultInstance, CT_NETBANKING, PAYMENT_INITIATED,
            null, null, bankName
        )

        val paymentMode = arrayListOf(WALLET_PAYMENT_METHOD)
        val walletData = WalletData(payCode)
        //val convenienceFeesData = ConvenienceFeesData(131040, 23785, 1100, 655925, 500000, 99999999, 155925, "INR")
        val deviceInfo = DeviceInfo(
            DeviceType.MOBILE.name,
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
            null, null, null, null, null, null,
            null, null, null, null, null
        )
        val extras = Extra(
            paymentMode,
            amount/*655925*/,
            currency,
            null,
            null,
            null,
            TransactionMode.REDIRECT.name,
            deviceInfo,
            null,
            null,
            Utils.createSDKData(requireActivity())
        )
        val processPaymentRequest =
            ProcessPaymentRequest(
                null,
                null,
                null,
                null,
                walletData,
                null,
                extras,
                null,
                null
            )
        return processPaymentRequest;
    }

    private fun setLiveDataListener() {
        mainViewModel.fetch_data_response.removeObservers(viewLifecycleOwner)
        mainViewModel.fetch_data_response.distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
                val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                    onLoading = {
                    }, onSuccess = { data ->
                        data?.paymentData?.originalTxnAmount?.let { transactionAmount ->
                            amount = transactionAmount.amount
                            currency = transactionAmount.currency
                        }

                        palette = data?.merchantBrandingData?.palette
                        orderId = data?.transactionInfo?.orderId

                        data?.paymentModes?.filter { paymentMode -> paymentMode.paymentModeId == PaymentModes.WALLET.paymentModeID }
                            ?.forEach { paymentMode ->
                                when (val pm = paymentMode?.paymentModeData) {
                                    is List<*> -> {
                                        val walletData = convertMapToJsonObject(pm)
                                        bankList = mapBankList(
                                            walletData
                                        )
                                        setNetBankingGrid()
                                    }
                                }
                            }

                        paymentModes = response.data?.paymentModes?.filter { paymentMode ->
                            paymentMode.paymentModeData != null || paymentMode.paymentModeId == PAYBYPOINTS_ID
                        }
                    }, onFailure = {

                    }
                )
                fetchDataResponseHandler.handleApiResult(response)
            }

    }

    private fun getWalletList(): List<WALLET> {
        return ArrayList<WALLET>(EnumSet.allOf(WALLET::class.java)).toList()
    }

    private fun mapBankList(
        walletList: List<Wallet>?,
    ): List<WalletBank?> {
        val netBankList = mutableListOf<WalletBank?>()
        walletList?.forEachIndexed() { index, wallet ->

            var nbbanks = getWalletList().singleOrNull { allWalletList ->
                allWalletList.walletName == wallet.bankName
            }

            var bank = if (nbbanks == null) {
                WalletBank(wallet.merchantPaymentCode, wallet.bankName, R.drawable.generic_wallet)
            } else {
                WalletBank(
                    wallet.merchantPaymentCode,
                    nbbanks.walletName,
                    nbbanks.walletImage
                )
            }
            netBankList.add(bank)
        }
        return netBankList
    }

    private fun setNetBankingGrid() {

        if (bankList?.size == 5) {

            recyclerNetBanks.visibility = View.GONE
            flexNetBanks.visibility = View.VISIBLE

            setBankGrid(view, bankList)

        } else {

            recyclerNetBanks.visibility = View.VISIBLE
            flexNetBanks.visibility = View.GONE

            val dividerItemDecoration: RecyclerView.ItemDecoration =
                GridDividerItemDecoration(requireActivity(), GridDividerItemDecoration.ALL)
            recyclerNetBanks.addItemDecoration(dividerItemDecoration)

            val gridBankAdapter =
                WalletAdapter(
                    bankList?.subList(0, if (bankList!!.size > 6) 6 else bankList!!.size)!!,
                    this@WalletFragment
                )

            val gridLayoutManager =
                GridLayoutManager(requireActivity(), calculateNoofRows(bankList?.size))

            recyclerNetBanks.layoutManager = gridLayoutManager
            recyclerNetBanks.adapter = gridBankAdapter
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

    private fun showMoreBanks() {
        val view =
            LayoutInflater.from(requireActivity()).inflate(R.layout.netbanking_all_banks, null)
        moreBanksBottomSheetDialog.setContentView(view)

        val btnClose: ImageView = view.findViewById(R.id.img_close)
        btnClose.setOnClickListener {
            moreBanksBottomSheetDialog.dismiss()
        }
        val edtSearchBank: EditText = view.findViewById(R.id.edt_search_banks)
        edtSearchBank.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                edtSearchBank.background = setColor(requireActivity())
            } else {
                edtSearchBank.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.edittext_default_border
                )
            }
        }
        edtSearchBank.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        val recyclerMoreBanks: RecyclerView = view.findViewById(R.id.recycler_net_all_banks)
        setMoreBankList(recyclerMoreBanks)
        moreBanksBottomSheetDialog.show()
    }

    private fun setMoreBankList(recyclerView: RecyclerView) {
        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        moreBankAdapter = WalletAllAdapter(
            bankList!!,
            this@WalletFragment
        )
        recyclerView.adapter = moreBankAdapter
    }

    private fun filter(text: String) {
        val filteredlist = mutableListOf<WalletBank>()

        bankList?.let { bankList ->
            for (item in bankList) {
                if (item?.bankName!!.toLowerCase().contains(text.toLowerCase())) {
                    filteredlist.add(item)
                }
            }
        }

        if (filteredlist.isEmpty()) {
            moreBankAdapter.filterList(mutableListOf())
        } else {
            moreBankAdapter.filterList(filteredlist.toList())
        }
    }

    fun calculateNoofRows(count: Int?): Int {
        return if (count == 1) 1 else if (count == 2 || count == 4) 2 else 3
    }

    fun setBankGrid(view: View?, bankList: List<WalletBank?>) {
        val linear1 = view?.findViewById<LinearLayout>(R.id.linear_1)
        val linear2 = view?.findViewById<LinearLayout>(R.id.linear_2)
        val linear3 = view?.findViewById<LinearLayout>(R.id.linear_3)
        val linear4 = view?.findViewById<LinearLayout>(R.id.linear_4)
        val linear5 = view?.findViewById<LinearLayout>(R.id.linear_5)

        linear1?.setOnClickListener {
            this.onItemClick(bankList[0])
        }
        linear2?.setOnClickListener {
            this.onItemClick(bankList[1])
        }
        linear3?.setOnClickListener {
            this.onItemClick(bankList[2])
        }
        linear4?.setOnClickListener {
            this.onItemClick(bankList[3])
        }
        linear5?.setOnClickListener {
            this.onItemClick(bankList[4])
        }

        val img1 = view?.findViewById<ImageView>(R.id.img_bank_1)
        val img2 = view?.findViewById<ImageView>(R.id.img_bank_2)
        val img3 = view?.findViewById<ImageView>(R.id.img_bank_3)
        val img4 = view?.findViewById<ImageView>(R.id.img_bank_4)
        val img5 = view?.findViewById<ImageView>(R.id.img_bank_5)

        val name1 = view?.findViewById<TextView>(R.id.txt_bank_name_1)
        val name2 = view?.findViewById<TextView>(R.id.txt_bank_name_2)
        val name3 = view?.findViewById<TextView>(R.id.txt_bank_name_3)
        val name4 = view?.findViewById<TextView>(R.id.txt_bank_name_4)
        val name5 = view?.findViewById<TextView>(R.id.txt_bank_name_5)

        img1?.setImageResource(bankList?.get(0)!!.bankImage)
        img2?.setImageResource(bankList?.get(1)!!.bankImage)
        img3?.setImageResource(bankList?.get(2)!!.bankImage)
        img4?.setImageResource(bankList?.get(3)!!.bankImage)
        img5?.setImageResource(bankList?.get(4)!!.bankImage)

        name1?.setText(bankList?.get(0)!!.bankName?.replace("_", " "))
        name2?.setText(bankList?.get(1)!!.bankName?.replace("_", " "))
        name3?.setText(bankList?.get(2)!!.bankName?.replace("_", " "))
        name4?.setText(bankList?.get(3)!!.bankName?.replace("_", " "))
        name5?.setText(bankList?.get(4)!!.bankName?.replace("_", " "))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.process_payment_response.removeObservers(this)
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

    fun convertMapToJsonObject(yourMap: List<*>): List<Wallet> {
        val gson = Gson().toJsonTree(yourMap).asJsonArray
        val listType: Type = object : TypeToken<ArrayList<Wallet?>?>() {}.type
        return Gson().fromJson(gson.toString(), listType)

    }
}

