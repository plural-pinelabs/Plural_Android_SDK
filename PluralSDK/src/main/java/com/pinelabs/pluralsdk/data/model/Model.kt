package com.pinelabs.pluralsdk.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FetchResponse(
    val transactionInfo: TransactionInfo? = null,
    val merchantInfo: MerchantInfo? = null,
    val paymentData: PaymentData? = null,
    val paymentModes: List<PaymentMode>? = null,
    var merchantBrandingData: MerchantBranding? = null,
    val customerInfo: CustomerInfo?,  // New field for customer information
    val shippingAddress: Address,
    val billingAddress: Address
)

data class Address(
    val address1: String?,
    val address2: String?,
    val pincode: String?,
    val city: String?,
    val state: String?,
    val country: String?
)

data class FetchError(
    val error_code: String,
    val error_message: String,
    val error_details: ErrorDetails?
)

data class ErrorDetails(val source: String, val error: ErrorDetailsError)

data class ErrorDetailsError(val code: String, val message: String, val next: List<String>)

data class FetchFailure(
    val status: String,
    val type: String,
    val message: String,
    val traceId: String
)

data class TransactionInfo(
    val orderId: String
)

data class MerchantInfo(
    val merchantId: Int,
    val merchantName: String,
    val merchantDisplayName: String?,
    val featureFlags: FeatureFlag?
)

data class FeatureFlag(
    var isSavedCardEnabled: Boolean?,
    var isNativeOTPEnabled: Boolean?,
    var isDCCEnabled: Boolean?
)

data class OrignalTransactionAmount(var amount: Int?, val currency: String)

data class PaymentData(var originalTxnAmount: OrignalTransactionAmount?)

data class PaymentMode(val paymentModeId: String, val paymentModeData: PaymentModeData?)

data class PaymentModeData(
    val upi_flows: List<String>?,
    val IssersUIDataList: List<issuerDataList>?
)

data class issuerDataList(val bankName: String, val merchantPaymentCode: String)

data class MerchantBranding(val logo: Logo?, val brandTheme: BrandTheme?, var palette: Palette?)

data class Logo(val imageSize: String, val imageContent: String)

data class BrandTheme(val color: String)

data class Palette(
    @SerializedName("50") val C50: String, @SerializedName("100") val C100: String,
    @SerializedName("200") val C200: String, @SerializedName("300") val C300: String,
    @SerializedName("400") val C400: String, @SerializedName("500") val C500: String,
    @SerializedName("600") val C600: String, @SerializedName("700") val C700: String,
    @SerializedName("800") val C800: String, @SerializedName("900") var C900: String
)

data class RecyclerViewPaymentOptionData(
    val payment_image: Int = -1,
    val payment_option: String = ""
)

// New data class to hold customer information
data class CustomerInfo(
    val customerId: String?,
    var customer_id: String?,
    val firstName: String?,
    var first_name: String?,
    val lastName: String,
    var last_name: String?,
    val isEditCustomerDetailsAllowed: Boolean?,
    var is_edit_customer_details_allowed: Boolean?,
    var is_edit: Boolean?,
    var countryCode: String?,
    var country_code: String?,
    var mobileNo: String?,
    var mobileNumber: String?,
    var mobile_number: String?,
    var email_id: String?,
    var emailId: String?,
    val totalTokens: Int?,
    val tokens: List<SavedCardTokens>?,
    var shippingAddress: Address? = null,
    var billingAddress: Address? = null,
    val shipping_address: Address? = null,
    val billing_address: Address? = null,
    val status: String?,
    val created_at: String?,
    val updated_at: String?
) : Serializable

data class CustomerInfoResponse(
    val status: String?,
    val customerInfo: CustomerInfo?
)

data class ProcessPaymentRequest(
    val card_token_data: CardTokenData?,
    val customer_data: CustomerData?,
    val card_data: CardData?,
    val upi_data: UpiData?,
    val netbanking_data: NetBankingData?,
    val extras: Extra?,
    val txn_data: UpiTransactionData?,
    val convenience_fee_data: ConvenienceFeesData?
) : Serializable

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
    val payment_id: String?,
    val order_id: String?,
    val short_link: String?,
)

data class CardTokenData(
    val token_id: String?,
    val cvv: String?
)

data class CustomerData(
    val mobileNo: String?,
    val email_id: String?
)

data class CardData(
    val card_number: String,
    val cvv: String,
    val card_holder_name: String,
    val card_expiry_year: String,
    val card_expiry_month: String,
    val isNativeOTPSupported: Boolean?,
    var save: Boolean?
)

data class DeviceInfo(
    val device_type: String?,
    val browser_user_agent: String?,

    val browser_accept_header: String?,
    val browser_language: String?,
    val browser_screen_height: String?,
    val browser_screen_width: String?,
    val browser_timezone: String?,
    val browser_window_size: String?,
    val browser_screen_color_depth: String?,
    val browser_java_enabled_val: Boolean?,
    val browser_javascript_enabled_val: Boolean?,
    val device_channel: String?,
    val browser_ip_address: String?
)

data class Extra(
    val payment_mode: List<String>?,
    val payment_amount: Int?,
    val payment_currency: String?,
    val card_last4: String?,
    val redeemable_amount: Int?,
    val registered_mobile_number: String?,
    val txn_mode: String?,
    val device_info: DeviceInfo?
)

data class PBPBank(
    val bankName: String,
    val bankLogo: String
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
    val status: String,
    val is_retry_available: Boolean
)

data class CancelTransactionResponse(
    val response_code: Int,
    val response_message: String
)

data class CancelResponseData(
    val order_id: String,
    val status: String,
    val signature: String
)

data class BinResponse(val GlobalBinsData: List<GlobalBinsData>, val resultInfo: ResultInfo)

data class GlobalBinsData(val issuerName: String, val cardType: String, val isDomesticCard: Boolean)

data class ResultInfo(val responseCode: String, val totalBins: String)

data class OTPRequest(
    val payment_id: String?,
    val otp: String?,
    val customerId: String?,
    val otpId: String?,
    val updateOrderDetails: UpdateOrderDetails?
)

data class OTPResponse(val next: List<String>?, val status: String?, val meta_data: MetaData?)

data class SavedCardResponse(val otpId: String?, val status: String?, val otpAttemptLeft: Int)

data class MetaData(val resend_after: String?)

data class CardBinMetaDataRequestList(val card_details: List<CardBinMetaDataRequest>)
data class CardBinMetaDataRequest(
    val payment_identifier: String,
    val payment_reference_type: String
)

data class CardBinMetaDataResponse(val card_payment_details: List<CardBinMetaDataResponseData>)
data class CardBinMetaDataResponseData(
    val payment_identifier: String,
    val payment_reference_type: String,
    val card_network: String,
    val card_issuer: String,
    val card_type: String,
    val card_category: String,
    val is_native_otp_supported: Boolean,
    val is_international_card: Boolean,
    val country_code: String,
    val currency: String,
    val is_currency_supported: Boolean
)

data class SavedCardData(val icon: Int, val text: String)

data class SavedCardTokens(
    val tokenId: String,
    val expiredAt: String,
    val cardData: SavedCardDataObject
)

data class SavedCardDataObject(
    val last4Digit: String,
    val networkName: String,
    val issuerName: String,
    val cvvRequired: Boolean
)

data class UpdateOrderDetails(
    val customer: CustomerInfo?
)