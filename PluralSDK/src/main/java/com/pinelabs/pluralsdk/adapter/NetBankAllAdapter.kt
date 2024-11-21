package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.PBPBank

class NetBankAllAdapter(
    private val items: List<PBPBank>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<NetBankAllAdapter.PaymentOptionDataHolder>() {

    inner class PaymentOptionDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionDataHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_bank_list_item, parent, false)
        return PaymentOptionDataHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentOptionDataHolder, position: Int) {
        val currentItem: PBPBank = items[position]

        val imgPaymentIcon: ImageView = holder.itemView.findViewById(R.id.img_bank)
        imgPaymentIcon.setImageResource(currentItem.bankLogo)

        val tvPaymentOption: TextView = holder.itemView.findViewById(R.id.txt_bank_name)
        tvPaymentOption.text = currentItem.bankName

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: PBPBank?)
    }

}