package com.pinelabs.pluralsdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.data.model.NetBank

class NetBankAllAdapterNew(
    private var bankList: List<NetBank?>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NetBankAllAdapterNew.NetBankAllDataHolder>() {

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
        val currentItem: NetBank? = bankList?.get(position)
        if (bankList?.get(position)?.bankName?.contains(":") == true){
            holder.bankName.text = bankList?.get(position)?.bankName?.split(":")?.get(1).toString()
        }
        else
            holder.bankName.text = bankList?.get(position)?.bankName.toString()

        //bankList?.get(position)?.bankImage?.let { holder.bankImage.setImageResource(it) }
        holder.bankImage.loadSvgOrOther(currentItem?.bankImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentItem)
        }
    }

    fun filterList(filterlist: List<NetBank>) {
        bankList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bankList!!.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: NetBank?)
    }
}
fun ImageView.loadSvgOrOther(myUrl: String?, cache: Boolean = true) {

    myUrl?.let {
        if (it.lowercase().endsWith("svg")) {
            val imageLoader = ImageLoader.Builder(this.context)
                .componentRegistry {
                    add(SvgDecoder(this@loadSvgOrOther.context))
                }.build()

            val request = ImageRequest.Builder(this.context).apply {
                /*error(errorImg)
                placeholder(errorImg)*/
                data(it).decoder(SvgDecoder(this@loadSvgOrOther.context))
            }.target(this).build()

            imageLoader.enqueue(request)
        } else {
            val imageLoader = ImageLoader(context)

            val request = ImageRequest.Builder(context).apply {
                if (cache) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                } else {
                    memoryCachePolicy(CachePolicy.DISABLED)
                }
                error(R.drawable.generic_bank)
                placeholder(R.drawable.generic_bank)
                data("$it")
            }.target(this).build()

            imageLoader.enqueue(request)
        }
    }

}

