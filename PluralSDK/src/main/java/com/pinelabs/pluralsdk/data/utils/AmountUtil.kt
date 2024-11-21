package com.pinelabs.pluralsdk.data.utils

import android.content.Context
import com.pinelabs.pluralsdk.R

object AmountUtil {
    fun convertToRupees(context: Context, amountInPaisa: Int): String {
        return context.getString(R.string.rs) + " " + (amountInPaisa.toDouble() / 100)
    }

    fun convertToPaisa(amountInRupees: Double): Double {
        return amountInRupees * 100
    }
}