package com.pinelabs.pluralsdk.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.Palette
import com.pinelabs.pluralsdk.data.model.SavedCardData

class SavedCardAdapter(private val savedCardList: List<SavedCardData>,private val palette: Palette?) :
    RecyclerView.Adapter<SavedCardAdapter.SavedCardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.saved_card_dialog_item,
            parent, false
        )
        return SavedCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.savedCardText.text = savedCardList[position].text
        holder.savedCardImage.setImageResource(savedCardList[position].icon)
        if (palette!=null){
            holder.savedCardImage.imageTintList = ColorStateList.valueOf(
                Color.parseColor(palette?.C900)
            )
        }
    }

    override fun getItemCount(): Int {
        return savedCardList.size
    }

    class SavedCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val savedCardText: TextView = itemView.findViewById(R.id.txt_save_card)
        val savedCardImage: ImageView = itemView.findViewById(R.id.img_saved_card)
    }
}