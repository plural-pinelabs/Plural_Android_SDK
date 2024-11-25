package com.pinelabs.plural_sdk_test_app.api.model

data class TokenRequest(val client_id: String, val client_secret: String, val grant_type: String)

data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_at: String
)

data class TokenFailure(
    val status: String,
    val type: String,
    val message: String,
    val traceId: String
)

data class OrderResponse(
    val token: String,
    val order_id: String,
    val redirect_url: String,
    val response_code: Int,
    val response_message: String
)

data class OrderRequest(
    var merchant_order_reference: String,
    val order_amount: OrderAmount,
    val pre_auth: Boolean,
    val purchase_details: PurchaseDetails,
    val integration_mode: String?
)

data class Order(
    val merchantId: Int,
    val pre_auth: Boolean,
    val order_amount: OrderAmount,
    val purchase_details: PurchaseDetails,
    val integration_mode: IntegrationMode
)

data class OrderAmount(val value: Int, val currency: String)

data class OrderFailure(val code: String, val message: String)

data class PurchaseDetails(val customer: Customer, val merchant_metadata: MerchantMetaData)

data class IntegrationMode(val integration_mode: String)

data class Customer(
    val email_id: String, val first_name: String, val last_name: String,
    val customer_id: String, val mobile_number: String, val billing_address: BillingAddress,
    val shipping_address: BillingAddress
)

data class BillingAddress(
    val address1: String, val address2: String, val address3: String,
    val pincode: String, val city: String, val state: String, val country: String
)

data class MerchantMetaData(val key1: String, val key2: String)
