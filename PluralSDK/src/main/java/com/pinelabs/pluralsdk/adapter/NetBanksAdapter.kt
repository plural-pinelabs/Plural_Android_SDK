package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.NBBANKS

class NetBanksAdapter(
    private val bankList: List<NBBANKS>?,
    private val itemClickListener: NetBankAllAdapter.OnItemClickListener
) : RecyclerView.Adapter<NetBanksAdapter.NetBankViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NetBankViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bank_list_item, parent, false)
        return NetBankViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NetBankViewHolder, position: Int) {
        val currentItem: NBBANKS? = bankList?.get(position)
        holder.bankName.text = bankList?.get(position)?.bankName.let { bankName ->
            if (bankName?.contains(":") == true) bankName?.split(":")
                ?.get(1) else bankName
        }?.replace("Bank", "")
        bankList?.get(position)?.bankImage?.let { holder.bankImage.setImageResource(it) }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return bankList!!.size
    }

    class NetBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.txt_bank_name)
        val bankImage: ImageView = itemView.findViewById(R.id.img_bank)
    }
}