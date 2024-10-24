package com.pinelabs.pluralsdk.utils

import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_REF
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_REF
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_REF
import com.pinelabs.pluralsdk.utils.Constants.Companion.WALLET_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.WALLET_REF

enum class PaymentModes(val paymentModeImage: Int, val paymentModeName: String, val paymentModeReference: String) {
    CREDIT_DEBIT(R.drawable.card, CREDIT_DEBIT_LABEL, CREDIT_DEBIT_REF),
    NET_BANKING(R.drawable.net_banking, NET_BANKING_LABEL, NET_BANKING_REF),
    UPI(R.drawable.upi, UPI_LABEL, UPI_REF),
    WALLET(R.drawable.upi, WALLET_LABEL, WALLET_REF)
}