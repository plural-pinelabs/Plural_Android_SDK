package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.ACSPageActivity
import com.pinelabs.pluralsdk.activity.FailureActivity
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.FlexDivider
import com.pinelabs.pluralsdk.adapter.GridDividerItemDecoration
import com.pinelabs.pluralsdk.adapter.NetBankAllAdapter
import com.pinelabs.pluralsdk.adapter.NetBanksAdapter
import com.pinelabs.pluralsdk.data.model.ConvenienceFeesData
import com.pinelabs.pluralsdk.data.model.DeviceInfo
import com.pinelabs.pluralsdk.data.model.Extra
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.NetBankingData
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.PaymentModeData
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.issuerDataList
import com.pinelabs.pluralsdk.data.utils.ApiResultHandler
import com.pinelabs.pluralsdk.utils.Constants.Companion.ERROR_MESSAGE
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_PAYMENT_METHOD
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.DeviceType
import com.pinelabs.pluralsdk.utils.NBBANKS
import com.pinelabs.pluralsdk.utils.PaymentModes
import com.pinelabs.pluralsdk.utils.TransactionMode
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel
import java.util.EnumSet


class NetBankingFragment : Fragment(), NetBankAllAdapter.OnItemClickListener {

    private lateinit var imgBack: ImageButton
    private lateinit var flexNetBanks: FlexboxLayout
    private lateinit var recyclerNetBanks: RecyclerView
    private lateinit var linearMoreBanks: LinearLayout
    private lateinit var moreBankAdapter: NetBankAllAdapter

    private lateinit var token: String
    private var amount: Int? = 0
    private lateinit var currency: String
    private var bankList: List<NBBANKS>? = mutableListOf()

    private var palette: Palette? = null

    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.netbanking_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = arguments?.getString(TOKEN).toString()

        //Backpress
        imgBack = view.findViewById(R.id.btnBack)
        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        flexNetBanks = view.findViewById(R.id.flex_net_banks_grid)
        //Setting Banks in Grid
        recyclerNetBanks = view.findViewById(R.id.recycler_net_banks_grid)
        setNetBankingGrid()


        //More bank button
        linearMoreBanks = view.findViewById(R.id.linear_more_banks)
        linearMoreBanks.setOnClickListener {
            showMoreBanks()
        }

        setLiveDataListener()

    }

    override fun onItemClick(item: NBBANKS?) {
        val bankCode =
            if (item!!.bankName!!.contains(":")) item!!.bankName!!.split(":")[0] else resources.getString(
                item!!.bankCode
            )
        val processPaymentRequest =
            createProcessPaymentRequest(bankCode, amount!!, currency)
        mainViewModel.processPayment(token, processPaymentRequest)
    }

    private fun createProcessPaymentRequest(
        payCode: String,
        amount: Int,
        currency: String
    ): ProcessPaymentRequest {
        val paymentMode = arrayListOf(NET_BANKING_PAYMENT_METHOD)
        val netBankingData = NetBankingData(payCode)
        //val convenienceFeesData = ConvenienceFeesData(131040, 23785, 1100, 655925, 500000, 99999999, 155925, "INR")
        val deviceInfo = DeviceInfo(
            DeviceType.MOBILE.name,
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
        )
        val extras = Extra(
            paymentMode,
            amount/*655925*/,
            currency,
            null,
            null,
            null,
            TransactionMode.REDIRECT.name,
            deviceInfo
        )
        val processPaymentRequest =
            ProcessPaymentRequest(null, null, netBankingData, extras, null, null)
        return processPaymentRequest;
    }

    private fun setLiveDataListener() {

        mainViewModel.fetch_response.observe(viewLifecycleOwner) { response ->
            val fetchDataResponseHandler = ApiResultHandler<FetchResponse>(requireActivity(),
                onLoading = {
                }, onSuccess = { data ->
                    data?.paymentData?.originalTxnAmount?.let { transactionAmount ->
                        amount = transactionAmount.amount
                        currency = transactionAmount.currency
                    }

                    palette = data?.merchantBrandingData?.palette

                    data?.paymentModes?.filter { paymentMode -> paymentMode.paymentModeId == PaymentModes.NET_BANKING.paymentModeID }
                        ?.forEach { paymentMode ->
                            val pm = paymentMode?.paymentModeData as? PaymentModeData
                            bankList = mapBankList( pm?.IssersUIDataList )
                            setNetBankingGrid()
                        }
                }, onFailure = {

                }
            )
            fetchDataResponseHandler.handleApiResult(response)
        }

        mainViewModel.process_payment_response.observe(viewLifecycleOwner) { response ->
            val fetchDataResponseHandler =
                ApiResultHandler<ProcessPaymentResponse>(requireActivity(),
                    onLoading = {

                    }, onSuccess = { response ->
                        val i = Intent(activity, ACSPageActivity::class.java)
                        i.putExtra(REDIRECT_URL, response!!.redirect_url)
                        startActivity(i)
                        requireActivity().finish()
                    }, onFailure = { errorMessage ->
                        val intent = Intent(requireActivity(), FailureActivity::class.java)
                        intent.putExtra(ERROR_MESSAGE, errorMessage)
                        startActivity(intent)
                        requireActivity().finish()
                    })
            fetchDataResponseHandler.handleApiResult(response)
        }

    }

    private fun getNBBankList(): List<NBBANKS> {
        return ArrayList<NBBANKS>(EnumSet.allOf(NBBANKS::class.java)).toList()
    }

    private fun mapBankList(issuerDataList: List<issuerDataList>?): List<NBBANKS>? {
        val netBankList = mutableListOf<NBBANKS>()
        issuerDataList?.forEach { issuerBank ->

            var nbbanks = getNBBankList().singleOrNull { bankList ->
                resources.getString(bankList.bankCode) == issuerBank.merchantPaymentCode
            }

            if (nbbanks == null) {
                nbbanks = NBBANKS.DEFAULT
                nbbanks.bankName = issuerBank.merchantPaymentCode + ":" + issuerBank.bankName
            } else nbbanks.bankName = issuerBank.bankName

            netBankList.add(nbbanks)
        }
        return netBankList
    }

    private fun setNetBankingGrid() {

        if (bankList?.size==5){

            recyclerNetBanks.visibility = View.GONE
            flexNetBanks.visibility = View.VISIBLE

            /*val flexLayoutManager = FlexboxLayoutManager(
                requireContext()
            ).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_EVENLY
            }

            val decor = FlexboxItemDecoration(requireContext())
            decor.setOrientation(FlexboxItemDecoration.BOTH)
            decor.setDrawable(resources.getDrawable(R.drawable.divider))
            recyclerNetBanks.addItemDecoration(decor)*/

            setBankGrid(view, bankList)

        } else {

            recyclerNetBanks.visibility = View.VISIBLE
            flexNetBanks.visibility = View.GONE

            val dividerItemDecoration: RecyclerView.ItemDecoration =
                GridDividerItemDecoration(requireActivity(), GridDividerItemDecoration.ALL)
            recyclerNetBanks.addItemDecoration(dividerItemDecoration)

            val gridBankAdapter =
                NetBanksAdapter(
                    bankList?.subList(0, if (bankList!!.size > 6) 6 else bankList!!.size),
                    this@NetBankingFragment
                )

            val gridLayoutManager = GridLayoutManager(requireActivity(),calculateNoofRows(bankList?.size))

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
        val moreBanksBottomSheetDialog = BottomSheetDialog(requireActivity())
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
        moreBankAdapter = NetBankAllAdapter(
            bankList,
            this@NetBankingFragment
        )
        recyclerView.adapter = moreBankAdapter
    }

    private fun filter(text: String) {
        val filteredlist = mutableListOf<NBBANKS>()

        bankList?.let { bankList ->
            for (item in bankList) {
                if (item.bankName.toLowerCase().contains(text.toLowerCase())) {
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
        return if (count==1) 1 else if (count==2 || count == 4) 2 else 3
    }

    fun setBankGrid(view: View?, bankList: List<NBBANKS>?) {
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

        name1?.setText(bankList?.get(0)!!.bankName)
        name2?.setText(bankList?.get(1)!!.bankName)
        name3?.setText(bankList?.get(2)!!.bankName)
        name4?.setText(bankList?.get(3)!!.bankName)
        name5?.setText(bankList?.get(4)!!.bankName)
    }
}

