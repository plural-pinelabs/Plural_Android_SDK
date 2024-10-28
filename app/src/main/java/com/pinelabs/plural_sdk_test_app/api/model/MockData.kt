package com.pinelabs.plural_sdk_test_app.api.model

class MockData {

    companion object {

        public val tokenRequest = TokenRequest("fbc0823a-ecdd-41c8-a40d-4449409c305b", "CE1083400E454594B79B4A310A89D128", "client_credentials")

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