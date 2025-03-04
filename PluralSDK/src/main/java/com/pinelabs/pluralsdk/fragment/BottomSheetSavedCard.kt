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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.SavedCardAdapter
import com.pinelabs.pluralsdk.data.model.SavedCardData

class BottomSheetSavedCard : BottomSheetDialogFragment() {

    private lateinit var recycler_savedCard: RecyclerView
    private var saveCardList = mutableListOf<SavedCardData>()

    private lateinit var btnSavedCardPay: Button
    private lateinit var btnWithoutPaying: Button
    private lateinit var btnOk: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_card_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSavedCardPay = view.findViewById(R.id.btnSavedCardPay)
        btnWithoutPaying = view.findViewById(R.id.btnWithoutPaying)
        btnOk = view.findViewById(R.id.btnOk)
        btnSavedCardPay.background = buttonBackground(requireActivity())
        btnSavedCardPay.isEnabled = true

        btnWithoutPaying.isEnabled = true

        btnOk.background = buttonBackground(requireActivity())
        btnOk.isEnabled = true


        recycler_savedCard = view.findViewById(R.id.saved_card_item)

        saveCardList.add(
            SavedCardData(
                R.drawable.saved_card_secure,
                getString(R.string.secure_transaction)
            )
        )
        saveCardList.add(
            SavedCardData(
                R.drawable.save_card_quicker,
                getString(R.string.quicker_transaction)
            )
        )
        saveCardList.add(
            SavedCardData(
                R.drawable.saved_card_everything,
                getString(R.string.everything_transaction)
            )
        )

        val savedCardAdapter = SavedCardAdapter(saveCardList)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_savedCard.adapter = savedCardAdapter
        recycler_savedCard.layoutManager = layoutManager
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