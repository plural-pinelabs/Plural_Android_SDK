package com.pinelabs.plural_sdk_test_app.api.model

class MockData {

    companion object {

        public val tokenRequest = TokenRequest("8ba31498-3eb1-4ac9-8eaf-4fc4620b528f", "206C7795C73E444C9C41359BEBF4B381", "client_credentials")

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