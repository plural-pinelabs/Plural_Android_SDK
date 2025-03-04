package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.SavedCardTokens

class SavedCardListAdapter(private val savedCardList: List<SavedCardTokens>) :
    RecyclerView.Adapter<SavedCardListAdapter.SavedCardViewHolder>() {

    private val cardIcons = mapOf(
        "AMEX" to R.drawable.amex,
        "VISA" to R.drawable.visa,
        "MASTERCARD" to R.drawable.mc,
        "RUPAY" to R.drawable.rupay,
        "Diners Club" to R.drawable.diners
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.saved_card_list_item,
            parent, false
        )
        return SavedCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.bankName.text = savedCardList.get(position).cardData.issuerName
        holder.bankImage.setImageResource(cardIcons[savedCardList.get(position).cardData.networkName]!!)
    }

    override fun getItemCount(): Int {
        return savedCardList.size
    }

    class SavedCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankImage: ImageView = itemView.findViewById(R.id.img_saved_card)
        val bankName: TextView = itemView.findViewById(R.id.txt_save_card_bank_name)
    }
}