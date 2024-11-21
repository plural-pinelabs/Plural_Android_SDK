package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.PBPBank

class FlexAdapter(private val bankList: List<PBPBank>) :
    RecyclerView.Adapter<FlexAdapter.PBPBankViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PBPBankViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.pbp_bank_list_item,
            parent, false
        )
        return PBPBankViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PBPBankViewHolder, position: Int) {
        holder.bankName.text = bankList.get(position).bankName
        holder.bankImage.setImageResource(bankList.get(position).bankLogo)
        /*val layoutParams = holder.itemView.layoutParams as FlexboxLayoutManager.LayoutParams
        layoutParams.flexGrow = 1.0f // Allow this item to grow and take available space
        layoutParams.flexShrink = 1.0f // Allow the item to shrink if necessary
        //layoutParams.alignSelf = FlexboxLayoutManager.LayoutParams.ce // Align the item to the center
        holder.itemView.layoutParams = layoutParams*/
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    class PBPBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.txt_bank_name)
        val bankImage: ImageView = itemView.findViewById(R.id.img_bank)
    }
}