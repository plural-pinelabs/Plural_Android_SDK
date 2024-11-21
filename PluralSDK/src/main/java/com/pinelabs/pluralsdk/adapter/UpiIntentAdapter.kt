package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R

class UpiIntentAdapter(
    private val bankList: List<Int>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<UpiIntentAdapter.PBPBankViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PBPBankViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.upi_image,
            parent, false
        )
        return PBPBankViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PBPBankViewHolder, position: Int) {
        holder.bankImage.setImageResource(bankList.get(position))
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    class PBPBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankImage: ImageView = itemView.findViewById(R.id.img_upi)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}