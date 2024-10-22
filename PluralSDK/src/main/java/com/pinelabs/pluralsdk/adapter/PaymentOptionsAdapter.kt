package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.RecyclerViewPaymentOptionData

class PaymentOptionsAdapter(private val items: List<RecyclerViewPaymentOptionData>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<PaymentOptionsAdapter.PaymentOptionDataHolder>() {

    inner class PaymentOptionDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionDataHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.payment_option_list_item, parent, false)
        return PaymentOptionDataHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentOptionDataHolder, position: Int) {
        val currentItem: RecyclerViewPaymentOptionData = items[position]

        val imgPaymentIcon: ImageView = holder.itemView.findViewById(R.id.img_payment_icon)
        imgPaymentIcon.setImageResource(currentItem.payment_image)

        val tvPaymentOption: TextView = holder.itemView.findViewById(R.id.txt_payment_option)
        tvPaymentOption.text = currentItem.payment_option

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: RecyclerViewPaymentOptionData?)
    }

}