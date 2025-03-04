package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.SavedCardData

class SavedCardAdapter(private val savedCardList: List<SavedCardData>) :
    RecyclerView.Adapter<SavedCardAdapter.SavedCardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.saved_card_item,
            parent, false
        )
        return SavedCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.savedCardText.text = savedCardList.get(position).text
        holder.savedCardImage.setImageResource(savedCardList.get(position).icon)
    }

    override fun getItemCount(): Int {
        return savedCardList.size
    }

    class SavedCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val savedCardText: TextView = itemView.findViewById(R.id.txt_save_card)
        val savedCardImage: ImageView = itemView.findViewById(R.id.img_saved_card)
    }
}