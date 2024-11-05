package com.pinelabs.pluralsdk.data.model

data class FetchResponse(
    val merchantInfo: MerchantInfo? = null,
    val paymentData: PaymentData? = null,
    val paymentModes: List<PaymentMode>? = null,
    val merchantBrandingData: MerchantBranding? = null,
    val customerInfo: CustomerInfo? = null  // New field for customer information
)

data class FetchError(val error_code:String, val error_message:String)

data class FetchFailure(val status: String, val type: String, val message: String, val traceId: String)

data class MerchantInfo(val merchantId: Int, val merchantName: String)

data class OrignalTransactionAmount(val amount: String, val currency: String)

data class PaymentData(val originalTxnAmount: OrignalTransactionAmount)

data class PaymentMode(val paymentModeId: String)

data class MerchantBranding(val logo: Logo, val brandTheme: BrandTheme)

data class Logo(val imageSize: String, val imageContent: String)

data class BrandTheme(val color: String)

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
    val extras: CardDataExtra,
    val upi_data: UpiData?
)

data class ProcessPaymentResponse(
    val redirect_url: String,
    val response_code: String,
    val response_message: String
)

data class CardData(
    val card_number: String,
    val cvv: String,
    val card_holder_name: String,
    val card_expiry_year: String,
    val card_expiry_month: String
)

data class CardDataExtra(
    val payment_mode: List<String>?,
    val payment_amount: String,
    val payment_currency: String,
)

data class UpiData(
    val upi_option: String,
    val vpa: String
)