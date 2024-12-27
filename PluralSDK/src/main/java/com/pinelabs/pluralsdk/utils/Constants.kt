package com.pinelabs.pluralsdk.utils

class Constants {
    companion object {
        const val APP_VERSION = "1.0"

        const val TOKEN = "TOKEN"
        const val ERROR_CODE = "ERROR_CODE"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val ERROR_MESSAGE_DEFAULT = "PAYMENT FAILED"
        const val REDIRECT_URL = "REDIRECT_URL"
        const val ORDER_ID = "ORDER_ID"
        const val PAYMENT_ID = "PAYMENT_ID"
        const val START_TIME= "START_TIME"
        const val SUCCESS_REDIRECT_URL = "savePluralPgTransactionStatus"

        const val SPLASH_TIMER: Long = 2300
        const val FAILURE_TIMER: Long = 5000
        const val UPI_TRANSACTION_STATUS_DELAY = 0L
        const val UPI_TRANSACTION_STATUS_INTERVAL = 5000L

        const val BASE_URL = "https://api-staging.pluralonline.com/api/v3/checkout-bff/"
        const val BASE_URL_QA = "https://pluralqa.pinepg.in/api/v3/checkout-bff/"

        const val API_INTERNET_MESSAGE = "No Internet Connection"
        const val API_ERROR = "API_ERROR"

        const val CREDIT_DEBIT_LABEL = "Cards"
        const val NET_BANKING_LABEL = "Netbanking"
        const val UPI_LABEL = "UPI"
        const val WALLET_LABEL = "WALLET"

        const val CREDIT_DEBIT_ID = "CREDIT_DEBIT"
        const val PAYBYPOINTS_ID = "PAYBYPOINTS"
        const val NET_BANKING_ID = "NET_BANKING"
        const val UPI_ID = "UPI"
        const val WALLET_ID = "WALLET"

        const val NET_BANKING_PAYMENT_METHOD = "NETBANKING"

        const val START_BOLD = "<b>"
        const val END_BOLD = "</b>"
        const val SPACE = " "

        const val BANK_ALLAHABAD = "Allahabad Bank"
        const val BANK_ANDHRA = "Andhra Bank"
        const val BANK_AU_SMALL = "AU Small Finance Bank"
        const val BANK_OF_INDIA = "Bank of India"
        const val BANK_CANARA = "Canara Bank"
        const val BANK_CENTRAL = "Central Bank"
        const val BANK_CORPORATION = "Corporation Bank"
        const val BANK_FEDERAL = "Federal Bank"
        const val BANK_IDBI = "IDBI Bank"
        const val BANK_IDFC = "IDFC FIRST Bank"
        const val BANK_IDIAN = "Indian Bank"
        const val BANK_KARUR_VYSYA = "Karur Vysya Bank"
        const val BANK_PUNJAB = "Punjab National Bank"
        const val BANK_SOUTH_INDIAN = "South Indian Bank"
        const val BANK_STATE_BANK = "State bank Of India"
        const val BANK_UNION = "Union Bank of India"
        const val BANK_YES_BANK = "Yes Bank"
        const val BANK_HDFC = "HDFC"
        const val BANK_SBI = "SBI"
        const val BANK_ICICI = "ICICI"
        const val BANK_AXIS = "AXIS"
        const val BANK_CITI = "CITI"
        const val BANK_PNB = "PNB"
        const val BANK_YES = "YES"

        //const val BANK_FEDERAL= "FEDERAL"
        const val BANK_KOTAK = "KOTAK"
        const val BANK_BOB = "BOB"

        //const val BANK_IDFC= "IDFC"
        const val BANK_INDIAN_OVERSEAS = "INDIAN OVERSEAS"
        const val BANK_ONECARD = "ONECARD"
        const val BANK_STANDARD_CHARTERED = "STANDARD CHARTERED"
        const val BANK_RBL = "RBL"

        const val GPAY = "com.google.android.apps.nbu.paisa.user"
        const val PHONEPE = "com.phonepe.app"
        const val PAYTM = "net.one97.paytm"

        const val UPI = "UPI"
        const val UPI_INTENT = "INTENT"
        const val UPI_COLLECT = "COLLECT"
        const val UPI_INTENT_PREFIX = "upi://pay"
        const val UPI_PAY_WITH = "Open with"
        const val UPI_PROCESSED_STATUS = "PROCESSED"
        const val UPI_PROCESSED_FAILED = "FAILED"

        const val TAG_PAYMENT_LISTING = "TAG_PAYMENT_LISTING"
        const val TAG_CARD = "TAG_CARD"
        const val TAG_UPI = "TAG_UPI"
        const val TAG_NETBANKING = "TAG_NETBANKING"

        const val CT_CARDS = "cards"
        const val CT_UPI = "upi"
        const val CT_COLLECT = "collect"
        const val CT_INTENT_GPAY = "google_pay"
        const val CT_INTENT_PHONEPE = "phonepe"
        const val CT_INTENT_PAYTM = "paytm"

        const val CT_NETBANKING = "netbanking"

        const val PAYMENT_INITIATED = "initiated"
        const val PAYMENT_DROPPED = "dropped"
    }
}