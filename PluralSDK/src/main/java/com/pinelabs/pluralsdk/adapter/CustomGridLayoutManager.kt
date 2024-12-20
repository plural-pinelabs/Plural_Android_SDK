package com.pinelabs.pluralsdk.adapter

import android.content.Context
import android.view.View
import com.google.android.flexbox.FlexboxLayoutManager

class CustomFlexboxLayoutManager(context: Context) : FlexboxLayoutManager(context) {

    override fun getFlexItemAt(index: Int): View {
        val item = super.getFlexItemAt(index)

        val params =
            item.layoutParams as LayoutParams

        params.isWrapBefore = index % 3 === 0
        return item
    }

}