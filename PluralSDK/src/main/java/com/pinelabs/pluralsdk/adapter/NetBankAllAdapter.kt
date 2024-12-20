package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.utils.NBBANKS

class NetBankAllAdapter(
    private var bankList: List<NBBANKS>?,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NetBankAllAdapter.NetBankAllDataHolder>() {

    class NetBankAllDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.txt_bank_name)
        val bankImage: ImageView = itemView.findViewById(R.id.img_bank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetBankAllDataHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_bank_list_item, parent, false)
        return NetBankAllDataHolder(view)
    }

    override fun onBindViewHolder(holder: NetBankAllDataHolder, position: Int) {
        val currentItem: NBBANKS? = bankList?.get(position)

        holder.bankName.text = bankList?.get(position)?.bankName.let { bankName ->
            if (bankName?.contains(":") == true) bankName?.split(":")
                ?.get(1) else bankName
        }
        bankList?.get(position)?.bankImage?.let { holder.bankImage.setImageResource(it) }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    fun filterList(filterlist: List<NBBANKS>) {
        bankList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bankList!!.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: NBBANKS?)
    }

}