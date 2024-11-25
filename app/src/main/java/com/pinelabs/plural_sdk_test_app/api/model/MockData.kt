package com.pinelabs.plural_sdk_test_app.api.model

class MockData {

    companion object {

        public val tokenRequest = TokenRequest(
            "64408363-22fa-48f7-a409-3f95bdee1696",
            "87600ED1BC80452685F5D9B7651BF373",
            "client_credentials"
        )

        private val orderAmount = OrderAmount(
            100,
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
            purchaseDetails,
            "IFRAME"
        )

    }

}