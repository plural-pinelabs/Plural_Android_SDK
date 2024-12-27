package com.pinelabs.plural_sdk_test_app.api.model

class MockData {

    companion object {

        //public val tokenRequest = TokenRequest("64408363-22fa-48f7-a409-3f95bdee1696", "87600ED1BC80452685F5D9B7651BF373", "client_credentials")
        //public val tokenRequest = TokenRequest("9436239f-6f44-4395-87df-dd3bb83c01a1", "221374903b514ac1a04424bf38c98186", "client_credentials")
        //public val tokenRequest = TokenRequest("111849bd-922b-4377-9da4-32c6f5d68083", "20DB51C6CCAE48CDB5DF50A54A465446", "client_credentials")
        //public val tokenRequest = TokenRequest("95c84ea2-aaa5-49c0-9fc4-47c0d6e3d0ca", "d7330d051e614b34bec7a6d7a13e9deb", "client_credentials")
        public val tokenRequest = TokenRequest("784a7c62-946d-4373-b9fe-325506e551b4", "dd28b3135e704c01817a74296af1a49a", "client_credentials") /*NB*/
        //public val tokenRequest = TokenRequest("3e534945-0a11-4496-801c-6eeb8f63ded0", "34e77c1cc8894c84bbfc5ef2b86c2b41", "client_credentials") /*NB*/
        //public val tokenRequest = TokenRequest("a43cbbc1-d1a4-4de4-a233-0100b1b0a747", "22A9CAF184F640588A20E8B673BB4072", "client_credentials")

        private val orderAmount = OrderAmount(
            500000,
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
            purchaseDetails/*,
            "IFRAME"*/
        )

    }

}