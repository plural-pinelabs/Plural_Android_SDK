package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.WalletBank

class WalletAllAdapter(
    private var bankList: List<WalletBank?>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WalletAllAdapter.WalletAllDataHolder>() {

    class WalletAllDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankName: TextView = itemView.findViewById(R.id.txt_bank_name)
        val bankImage: ImageView = itemView.findViewById(R.id.img_bank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletAllDataHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_bank_list_item, parent, false)
        return WalletAllDataHolder(view)
    }

    override fun onBindViewHolder(holder: WalletAllDataHolder, position: Int) {
        val currentItem: WalletBank? = bankList?.get(position)

        holder.bankName.text = bankList?.get(position)?.bankName?.replace("_", " ")

        //bankList?.get(position)?.bankImage?.let { holder.bankImage.setImageResource(it) }
        holder.bankImage.setImageResource(currentItem?.bankImage!!)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    fun filterList(filterlist: List<WalletBank>) {
        bankList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bankList!!.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: WalletBank?)
    }
}