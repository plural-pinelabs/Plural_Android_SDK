package com.pinelabs.plural_sdk_test_app.api.model

class MockData {

    companion object {

        public val tokenRequest = TokenRequest("3e534945-0a11-4496-801c-6eeb8f63ded0", "34e77c1cc8894c84bbfc5ef2b86c2b41", "client_credentials")

        private val orderAmount = OrderAmount(
            50000,
            "INR"
        )

        private val billingAddress = BillingAddress(
            "H.No 15, Sector 17",
            "",
            "",
            "61232112",
            "CHANDIGARH",
            "PUNJAB",
            "INDIA"
        )

        private val shippingAddress = BillingAddress(
            "H.No 15, Sector 17",
            "string",
            "string",
            "144001123",
            "CHANDIGARH",
            "PUNJAB",
            "INDIA"
        )

        private val customer = Customer(
            "joe.sam@gmail.com",
            "joe",
            "kumar",
            "192212",
            "905002003",
            billingAddress,
            shippingAddress
        )

        private val merchantMetaData = MerchantMetaData(
            "value1",
            "value2"
        )

        val purchaseDetails = PurchaseDetails(
            customer,
            merchantMetaData
        )

        val orderUAT = OrderRequest(
            "",
            orderAmount,
            false,
            purchaseDetails
        )

    }

}