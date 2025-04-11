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

    fun roundToDecimal(amount: Double): String {
        val df = DecimalFormat("0.00")
        return df.format(amount)
    }

    fun transformAmount(ratio: Int?, amountInPaisa: Int?): String {
        if (ratio!! > 1) {
            val divideBy = Math.pow("10".toDouble(), ratio.toDouble())
            return amountInPaisa?.div(divideBy).toString()
        } else
            return amountInPaisa.toString()
    }

    fun convertTransformation(ratio: Int?, amount: Double?): String {
        if (ratio!! > 1) {
            val multiply = Math.pow("10".toDouble(), ratio.toDouble())
            val value = amount?.times(multiply)
            return roundToDecimal(value!!)
        } else {
            return amount.toString()!!
        }

    }
}