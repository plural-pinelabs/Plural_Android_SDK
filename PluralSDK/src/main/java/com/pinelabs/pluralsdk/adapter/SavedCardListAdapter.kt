package com.pinelabs.pluralsdk.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.SavedCardTokens
import com.pinelabs.pluralsdk.data.utils.AmountUtil
import com.pinelabs.pluralsdk.data.utils.Utils.buttonBackground
import com.pinelabs.pluralsdk.utils.Constants.Companion.SPACE

class SavedCardListAdapter(
    val context: Context,
    private val savedCardList: List<SavedCardTokens>?,
    val itemClick: OnItemClickListener,
    val palette: Palette?,
    val isAddCard: Boolean,
    val amount: Int?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_REGULAR = 0
    private val ITEM_TYPE_FOOTER = 1

    private var selectedPosition = -1

    private val cardIcons = mapOf(
        "AMEX" to R.drawable.amex,
        "VISA" to R.drawable.visa,
        "MASTERCARD" to R.drawable.mc,
        "RUPAY" to R.drawable.rupay,
        "DINERS/DISCOVER" to R.drawable.diners
    )

    // ViewHolder class to hold views
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearSavedCard: LinearLayout = itemView.findViewById(R.id.linear_saved_card_item)
        val bankImage: ImageView = itemView.findViewById(R.id.img_saved_card)
        val bankName: TextView = itemView.findViewById(R.id.txt_save_card_bank_name)
        val last4Digit: TextView = itemView.findViewById(R.id.txt_last_4)
        val cbSelect: CheckBox = itemView.findViewById(R.id.img_check)
        val etCVV: EditText = itemView.findViewById(R.id.etCVV)
        val imgCVVRequired: ImageView = itemView.findViewById(R.id.img_cvv_required)
        val btnProceed: Button = itemView.findViewById(R.id.btnProceedToPay)
    }

    // Footer ViewHolder
    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constrainAddCard: ConstraintLayout = itemView.findViewById(R.id.constrain_add_new_card)
        val txtAddCard: ImageView = itemView.findViewById(R.id.img_new_card)
        val txtViewAllCard: ImageView = itemView.findViewById(R.id.img_view_saved_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ITEM_TYPE_FOOTER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.saved_card_add_card, parent, false)
                FooterViewHolder(itemView)
            }

            else -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.saved_card_list_item, parent, false)
                MyViewHolder(itemView)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            // Bind regular item data
            holder.bankName.text = savedCardList?.get(position)?.cardData?.issuerName
            holder.bankImage.setImageResource(cardIcons[savedCardList?.get(position)?.cardData?.networkName]!!)
            holder.last4Digit.text = savedCardList?.get(position)?.cardData?.last4Digit
            holder.btnProceed.background = buttonBackground(context, palette)
            holder.btnProceed.isEnabled = true
            holder.btnProceed.setText(
                context.getString(R.string.pay) + SPACE + AmountUtil.convertToRupees(
                    context,
                    amount!!
                )
            )
            holder.btnProceed.setOnClickListener {
                val cvv = holder.etCVV.text.toString()
                itemClick.OnPaymentClick(
                    savedCardList?.get(position),
                    cvv.ifEmpty { null }
                )
            }

            if (savedCardList?.get(position)?.cardData?.cvvRequired == true) {
                holder.imgCVVRequired.visibility = View.GONE
                holder.etCVV.visibility = View.VISIBLE
                holder.cbSelect.visibility = View.GONE
            } else {
                holder.imgCVVRequired.visibility = View.VISIBLE
                holder.etCVV.visibility = View.GONE
                holder.cbSelect.visibility = View.VISIBLE
            }

            holder.cbSelect.setOnClickListener {
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
            }

            holder.etCVV.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    holder.linearSavedCard.setBackgroundColor(
                        ColorUtils.setAlphaComponent(
                            context.getColor(
                                R.color.header_color
                            ), 15
                        )
                    )
                    holder.btnProceed.visibility = View.VISIBLE
                } else {
                    holder.linearSavedCard.setBackgroundColor(context.getColor(R.color.white))
                    holder.etCVV.clearFocus()
                    holder.btnProceed.visibility = View.GONE
                }
            }

            if (selectedPosition == position) {
                holder.cbSelect.setChecked(true)
                holder.linearSavedCard.setBackgroundColor(
                    ColorUtils.setAlphaComponent(
                        context.getColor(
                            R.color.header_color
                        ), 15
                    )
                )
                holder.btnProceed.visibility = View.VISIBLE
                if (palette != null) {
                    val layerDrawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.save_card_check_layer
                    ) as LayerDrawable
                    val gradientDrawable =
                        layerDrawable.findDrawableByLayerId(R.id.icon_bg) as VectorDrawable
                    gradientDrawable.setTint(Color.parseColor(palette?.C900))
                    holder.cbSelect.background = layerDrawable
                    /*holder.linearSavedCard.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(palette?.C900))*/
                } else {
                    holder.cbSelect.background =
                        context.getDrawable(R.drawable.save_card_check_layer)
                }
            } else {
                holder.cbSelect.setChecked(false)
                holder.cbSelect.background = context.getDrawable(R.drawable.check_circle)
                holder.linearSavedCard.setBackgroundColor(context.getColor(R.color.white))
                holder.btnProceed.visibility = View.GONE
            }

            if (palette != null) {
                holder.imgCVVRequired.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(palette?.C900)
                )
            }

        } else if (holder is FooterViewHolder) {
            if (palette != null) {
                holder.txtAddCard.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(palette?.C900)
                )
            }
            if (isAddCard) {
                holder.txtAddCard.visibility = View.VISIBLE
                holder.txtViewAllCard.visibility = View.GONE
            } else {
                holder.txtViewAllCard.visibility = View.VISIBLE
                holder.txtAddCard.visibility = View.GONE
            }

            holder.txtViewAllCard.setOnClickListener {
                itemClick.onViewAllCards()
            }

            holder.txtAddCard.setOnClickListener {
                itemClick.onAddCard()
            }

        }
    }

    override fun getItemCount(): Int {
        return savedCardList?.size!! + 1 // One extra item for the footer
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == savedCardList?.size) ITEM_TYPE_FOOTER else ITEM_TYPE_REGULAR
    }

    interface OnItemClickListener {
        fun OnPaymentClick(item: SavedCardTokens?, cvv: String?)
        fun onAddCard()
        fun onViewAllCards()
    }

}