package com.pinelabs.pluralsdk.data.model

import com.google.gson.annotations.SerializedName

data class FetchResponse(
    val merchantInfo: MerchantInfo? = null,
    val paymentData: PaymentData? = null,
    val paymentModes: List<PaymentMode>? = null,
    val merchantBrandingData: MerchantBranding? = null,
    val customerInfo: CustomerInfo? = null  // New field for customer information
)

data class FetchError(val error_code: String, val error_message: String)

data class FetchFailure(
    val status: String,
    val type: String,
    val message: String,
    val traceId: String
)

data class MerchantInfo(val merchantId: Int, val merchantName: String)

data class OrignalTransactionAmount(var amount: Int?, val currency: String)

data class PaymentData(var originalTxnAmount: OrignalTransactionAmount?)

data class PaymentMode(val paymentModeId: String, val paymentModeData: Any?)

data class PaymentModeData(
    val upi_flows: List<String>?,
    val IssersUIDataList: List<issuerDataList>?
)

data class issuerDataList(val bankName: String, val merchantPaymentCode: String)

data class MerchantBranding(val logo: Logo?, val brandTheme: BrandTheme?, val palette: Palette?)

data class Logo(val imageSize: String, val imageContent: String)

data class BrandTheme(val color: String)

data class Palette(@SerializedName("50") val C50: String, @SerializedName("100") val C100: String,
                   @SerializedName("200") val C200: String, @SerializedName("300") val C300: String,
                   @SerializedName("400") val C400: String, @SerializedName("500") val C500: String,
                   @SerializedName("600") val C600: String, @SerializedName("700") val C700: String,
                   @SerializedName("800") val C800: String, @SerializedName("900") val C900: String)

data class RecyclerViewPaymentOptionData(
    val payment_image: Int = -1,
    val payment_option: String = ""
)

// New data class to hold customer information
data class CustomerInfo(
    val mobileNo: String,
    val emailId: String
)

data class ProcessPaymentRequest(
    val card_data: CardData?,
    val upi_data: UpiData?,
    val netbanking_data: NetBankingData?,
    val extras: Extra?,
    val txn_data: UpiTransactionData?,
    val convenience_fee_data: ConvenienceFeesData?
)

data class ConvenienceFeesData(
    val convenience_fees_amt_in_paise: Int,
    val convenience_fees_fees_gst_amt_in_paise: Int,
    val convenience_fees_fees_addition_amt_in_paise: Int,
    val final_amt_in_paise: Int,
    val transaction_amount: Int,
    val convenience_fees_maximum_fee_amount: Int,
    val convenience_fees_applicable_fee_amount: Int,
    val currency: String
)

data class NetBankingData(
    val pay_code: String
)

data class ProcessPaymentResponse(
    val redirect_url: String?,
    val response_code: String,
    val response_message: String,
    val pg_upi_unique_request_id: String?,
    val deep_link: String?,
    val order_id: String?,
    val short_link: String?,
)

data class CardData(
    val card_number: String,
    val cvv: String,
    val card_holder_name: String,
    val card_expiry_year: String,
    val card_expiry_month: String
)

data class DeviceInfo(
    val device_type: String,
    val browser_user_agent: String
)

data class Extra(
    val payment_mode: List<String>?,
    val payment_amount: Int?,
    val payment_currency: String,
    val card_last4: String?,
    val redeemable_amount: Int?,
    val registered_mobile_number: String?,
    val txn_mode: String?,
    val device_info: DeviceInfo?
)

data class PBPBank(
    val bankName: String,
    val bankLogo: Int
)

data class UpiData(
    val upi_option: String,
    val vpa: String?,
    val txn_mode: String?
)

data class UpiTransactionData(
    val SelectedPaymentModeId: Int
)

data class RewardRequest(
    val payment_method: String,
    val payment_option: RewardPaymentOption,
    val order_details: OrderDetails
)

data class RewardPaymentOption(
    val points_card_details: RewardPointsCardDetails
)

data class RewardPointsCardDetails(
    val card_last4: String,
    val card_number: String,
    val registered_mobile_number: String?
)

data class OrderDetails(
    val order_amount: OrderDetailsAmount
)

data class OrderDetailsAmount(
    val value: Int,
    val currency: String
)

data class RewardResponse(
    val payment_method: String,
    val is_eligible: Boolean,
    val payment_option_metadata: PaymentOptionMetaData,
    val redeemable_amount: OrderDetailsAmount,
    val balance: OrderDetailsAmount
)

data class PaymentOptionMetaData(
    val pay_by_point_option_data: PBPOptionData
)

data class PBPOptionData(
    var redeemable_points: Int
)

data class TransactionStatusResponse(
    val data: TransactionStatus
)

data class TransactionStatus(
    val order_id: String,
    val status: String
)

data class CancelTransactionResponse(
    val responsePage: String,
    val responseData: CancelResponseData
)

data class CancelResponseData(
    val order_id: String,
    val status: String,
    val signature: String
)