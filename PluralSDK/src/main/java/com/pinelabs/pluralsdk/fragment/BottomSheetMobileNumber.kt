package com.pinelabs.pluralsdk.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R

class BottomSheetMobileNumber: BottomSheetDialogFragment() {

    private lateinit var btnSavedCardPay: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_card_mobile_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSavedCardPay = view.findViewById(R.id.btnProceedToPay)
        btnSavedCardPay.background = buttonBackground(requireActivity())
    }

    public fun buttonBackground(context: Context): Drawable {

        val stateListDrawable = StateListDrawable()

        // Create different drawables for different states
        val pressedDrawable = GradientDrawable().apply {
            /*if (palette != null) {
                setColor(Color.parseColor(palette?.C900))
            } else {*/
            setColor(context.resources.getColor(R.color.header_color))
            /*}*/
            cornerRadius = 16f // Normal corner radius
        }

        // Add states to the StateListDrawable
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), pressedDrawable) // Default state

        return stateListDrawable
    }

}