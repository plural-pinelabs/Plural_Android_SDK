package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.RecyclerViewPaymentOptionData
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYBYPOINTS_REF
import com.pinelabs.pluralsdk.utils.PaymentModes

class PaymentOptionsAdapter(
    private val items: List<RecyclerViewPaymentOptionData>,
    private val paymentOption: List<String>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<PaymentOptionsAdapter.PaymentOptionDataHolder>() {

    inner class PaymentOptionDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionDataHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_option_list_item, parent, false)
        return PaymentOptionDataHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentOptionDataHolder, position: Int) {
        val currentItem: RecyclerViewPaymentOptionData = items[position]

        val imgPaymentIcon: ImageView = holder.itemView.findViewById(R.id.img_payment_icon)
        imgPaymentIcon.setImageResource(currentItem.payment_image)

        val tvPaymentOption: TextView = holder.itemView.findViewById(R.id.txt_payment_option)
        tvPaymentOption.text = currentItem.payment_option

        val imgRewardIcon: ImageView = holder.itemView.findViewById(R.id.redeem_points)
        if (currentItem.payment_option.equals(PaymentModes.CREDIT_DEBIT.paymentModeName) && paymentOption.contains(
                PAYBYPOINTS_REF
            )
        ) {
            imgRewardIcon.visibility = View.VISIBLE
        } else {
            imgRewardIcon.visibility = View.GONE
        }

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