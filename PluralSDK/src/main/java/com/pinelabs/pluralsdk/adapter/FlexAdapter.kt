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

class FlexAdapter(private val bankList: List<PBPBank>, private val count: Int) :
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
        holder.bankImage.loadSvgOrOther(bankList.get(position).bankLogo)
    }

    override fun getItemCount(): Int {
        return (if (count >= bankList.size) bankList.size else count)
    }

    class PBPBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.txt_bank_name)
        val bankImage: ImageView = itemView.findViewById(R.id.img_bank)
    }
}