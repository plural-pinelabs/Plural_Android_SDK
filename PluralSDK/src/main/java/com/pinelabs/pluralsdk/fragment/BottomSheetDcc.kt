package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.loadSvgOrOther
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponseData
import com.pinelabs.pluralsdk.data.model.DCCDetails
import com.pinelabs.pluralsdk.data.model.DccData
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.utils.AmountUtil
import com.pinelabs.pluralsdk.data.utils.Utils
import com.pinelabs.pluralsdk.utils.Constants.Companion.AMOUNT
import com.pinelabs.pluralsdk.utils.Constants.Companion.BIN_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.DCC_DATA
import com.pinelabs.pluralsdk.utils.Constants.Companion.INR
import com.pinelabs.pluralsdk.utils.Constants.Companion.PALETTE
import com.pinelabs.pluralsdk.utils.Constants.Companion.PROCESS_PAYMENT_REQUEST
import com.pinelabs.pluralsdk.utils.Constants.Companion.TAG_ADDRESS
import com.pinelabs.pluralsdk.utils.Constants.Companion.TOKEN
import com.pinelabs.pluralsdk.utils.DCC_STATUS
import com.pinelabs.pluralsdk.viewmodels.FetchDataViewModel


class BottomSheetDcc : BottomSheetDialogFragment() {

    private var linearOtherCurrency: LinearLayout? = null
    private var linearIndianCurrency: LinearLayout? = null
    private var imgOtherCurrency: ImageView? = null
    private var imgIndianCurrency: ImageView? = null
    private var txtOtherCurrency: TextView? = null
    private var txtIndianCurrency: TextView? = null
    private var txtPayingIn: TextView? = null
    private var btnPay: Button? = null

    private var palette: Palette? = null
    private var dccData: DccData? = null
    private var dccDetails: DCCDetails? = null
    private var amount: Int? = null
    private var token: String? = null
    private var currency: String? = INR
    private var amountPassed: Int? = 0
    private var transformationRatio: Int? = null
    private var currencySymbol: String? = null
    private var binData: CardBinMetaDataResponseData? = null
    private var processPaymentRequest: ProcessPaymentRequest? = null

    private val mainViewModel by activityViewModels<FetchDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dcc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dccData = arguments?.getParcelable(DCC_DATA)
        binData = arguments?.getParcelable(BIN_DATA)
        palette = arguments?.getParcelable(PALETTE)
        amount = arguments?.getInt(AMOUNT)
        token = arguments?.getString(TOKEN)
        processPaymentRequest =
            arguments?.getParcelable<ProcessPaymentRequest>(PROCESS_PAYMENT_REQUEST)

        linearIndianCurrency = view.findViewById(R.id.linear_indian_currency)
        linearOtherCurrency = view.findViewById(R.id.linear_other_currency)

        txtOtherCurrency = view.findViewById(R.id.txt_other_country)
        txtIndianCurrency = view.findViewById(R.id.txt_indian_country)
        txtPayingIn = view.findViewById(R.id.txt_paying_in)

        imgIndianCurrency = view.findViewById(R.id.img_indian_country)
        imgOtherCurrency = view.findViewById(R.id.img_other_country)

        dccData?.currencyMapper?.get(binData?.currency).let { country ->
            imgOtherCurrency?.loadSvgOrOther(country?.flag, false)
            txtOtherCurrency?.text = country?.symbol + " " + AmountUtil.transformAmount(
                country?.transformation_ratio,
                binData?.converted_amount
            )
            transformationRatio = country?.transformation_ratio
            currencySymbol = country?.symbol
        }
        dccData?.currencyMapper?.get(INR).let { country ->
            imgIndianCurrency?.loadSvgOrOther(country?.flag, false)
            txtIndianCurrency?.text =
                amount?.let {
                    AmountUtil.convertToRupees(
                        requireActivity(),
                        it
                    )
                }
        }

        btnPay = view.findViewById(R.id.btnProceedToPay)
        btnPay?.background = Utils.buttonBackground(requireActivity(), palette)
        btnPay?.text =
            amount?.let {
                AmountUtil.convertToRupees(
                    requireActivity(),
                    it
                )
            }

        btnPay?.isEnabled = true
        btnPay?.setOnClickListener {
            if (amountPassed!! > 0) {
                processPaymentRequest?.extras?.payment_amount = amountPassed
                processPaymentRequest?.extras?.payment_currency = currency
            }
            if (processPaymentRequest?.extras?.dcc_status == null) {
                if (currency.equals(INR))
                    processPaymentRequest?.extras?.dcc_status =
                        DCC_STATUS.ORGANIC_OPT_OUT.toString()
                else
                    processPaymentRequest?.extras?.dcc_status =
                        DCC_STATUS.OPT_IN.toString()
            }
            this.dismiss()
            showAddressPage(processPaymentRequest)
        }

        linearIndianCurrency?.setOnClickListener {

            linearIndianCurrency?.background = setColor(requireActivity())
            linearOtherCurrency?.background =
                resources.getDrawable(R.drawable.edittext_default_border)
            txtPayingIn?.text = getString(R.string.dcc_paying_in)
            btnPay?.text = getString(R.string.pay) + " " + txtIndianCurrency?.text.toString()
            this.currency = INR
            this.amountPassed = amount
            mainViewModel.dccAmount.value = txtIndianCurrency?.text.toString()
            dccDetails = DCCDetails(amount, null, null, null, null, null, null)
            mainViewModel.dccAmountMessage.value = dccDetails
        }

        linearOtherCurrency?.setOnClickListener {
            linearOtherCurrency?.background = setColor(requireActivity())
            linearIndianCurrency?.background =
                resources.getDrawable(R.drawable.edittext_default_border)
            val conversionRatio = binData?.conversion_rate?.let {
                AmountUtil.roundToDecimal(
                    it
                )
            }
            txtPayingIn?.text = "1" + " " + INR + "=" + conversionRatio + " " + binData?.currency
            btnPay?.text = getString(R.string.pay) + " " + txtOtherCurrency?.text.toString()
            this.currency = binData?.currency
            this.amountPassed = binData?.converted_amount
            mainViewModel.dccAmount.value = txtOtherCurrency?.text.toString()
            dccDetails = DCCDetails(
                amount,
                binData?.currency,
                binData?.converted_amount,
                currencySymbol,
                transformationRatio,
                null,
                binData?.conversion_rate
            )
            mainViewModel.dccAmountMessage.value = dccDetails
        }

        linearOtherCurrency?.performClick()
        /*linearIndianCurrency?.setOnTouchListener { v, event ->
            linearIndianCurrency?.background =
                resources.getDrawable(R.drawable.edittext_border_focussed)
            linearOtherCurrency?.background =
                resources.getDrawable(R.drawable.edittext_default_border)
            txtPayingIn?.text = getString(R.string.dcc_paying_in)
            btnPay?.text = getString(R.string.pay) + " " + txtIndianCurrency?.text.toString()
            this.currency = INR
            this.amountPassed = amount
            mainViewModel.dccAmount.value = txtIndianCurrency?.text.toString()
            dccDetails = DCCDetails(amount, null, null, null)
            mainViewModel.dccAmountMessage.value = dccDetails
            false
        }
        linearOtherCurrency?.setOnTouchListener { v, event ->
            linearOtherCurrency?.background =
                resources.getDrawable(R.drawable.edittext_border_focussed)
            linearIndianCurrency?.background =
                resources.getDrawable(R.drawable.edittext_default_border)
            val conversionRatio = binData?.conversion_rate?.let {
                AmountUtil.roundToDecimal(
                    it
                )
            }
            txtPayingIn?.text = "1" + " " + INR + "=" + conversionRatio + " " + binData?.currency
            btnPay?.text = getString(R.string.pay) + " " + txtOtherCurrency?.text.toString()
            this.currency = binData?.currency
            this.amountPassed = binData?.converted_amount
            mainViewModel.dccAmount.value = txtOtherCurrency?.text.toString()
            dccDetails = DCCDetails(
                amount,
                binData?.currency,
                binData?.converted_amount,
                transformationRatio
            )
            false
        }*/
    }

    fun showAddressPage(processPaymentRequest: ProcessPaymentRequest?) {
        val arguments = Bundle()
        arguments.putParcelable(
            PROCESS_PAYMENT_REQUEST,
            processPaymentRequest
        )
        arguments.putString(TOKEN, token)
        arguments.putString(AMOUNT, btnPay?.text.toString())
        arguments.putParcelable(PALETTE, palette)

        val addressFragment = AddressFragment()
        addressFragment.arguments = arguments

        requireActivity().supportFragmentManager.popBackStack()
        val transaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.details_fragment,
            addressFragment,
            TAG_ADDRESS
        )
        transaction.addToBackStack(TAG_ADDRESS)
        transaction.commit()
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