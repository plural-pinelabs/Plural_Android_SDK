package com.pinelabs.pluralsdk.utils

import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.Constants.Companion.ALL_PAYMENT_METHODS_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.CREDIT_DEBIT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.NET_BANKING_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.WALLET_LABEL
import com.pinelabs.pluralsdk.utils.Constants.Companion.WALLET_ID

enum class PaymentModes(
    val paymentModeImage: Int,
    val paymentModeName: String,
    val paymentModeID: String
) {
    CREDIT_DEBIT(R.drawable.card, CREDIT_DEBIT_LABEL, CREDIT_DEBIT_ID),
    NET_BANKING(R.drawable.net_banking, NET_BANKING_LABEL, NET_BANKING_ID),
    UPI(R.drawable.upi, UPI_LABEL, UPI_ID),
    WALLET(R.drawable.upi, WALLET_LABEL, WALLET_ID),
    ALL_PAYMENT(R.drawable.all_payment_modes, ALL_PAYMENT_METHODS_LABEL, "")
}

enum class TransactionMode {
    REDIRECT
}

enum class DeviceType {
    WEB,
    MOBILE,
    TABLET
}

enum class PaymentModeId(val id: Int) {
    CREDIT_DEBIT(1),
    NETBANKING(3),
    UPI(10),
    EMI(4),
    WALLET(11),
    PBP(15),
    DEBIT_EMI(14),
    CARDLESS_EMI(19)
}
