package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import com.pinelabs.pluralsdk.R
import java.text.DecimalFormat

object AmountUtil {
    fun convertToRupees(context: Context, amountInPaisa: Int): String {
        return context.getString(R.string.rs) + " " + roundToDecimal(amountInPaisa.toDouble() / 100)
    }

    fun convertToPaisa(amountInRupees: Double): Double {
        return amountInRupees * 100
    }

    fun roundToDecimal(amount: Double): String{
        val df = DecimalFormat("#.00")
        return df.format(amount)
    }
}