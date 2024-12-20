package com.pinelabs.pluralsdk.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecoratorHorizontal
import com.pinelabs.pluralsdk.adapter.UpiIntentAdapter

class UpiIntentActivity : AppCompatActivity(), UpiIntentAdapter.OnItemClickListener {
    val upiList =
        mutableListOf<Int>/*()*/(R.drawable.google_pay/*, R.drawable.phone_pe, R.drawable.paytm*/)

    val payTm = "net.one97.paytm"
    val gpay = "com.google.android.apps.nbu.paisa.user"
    val phonePe = "com.phonepe.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upi_intent)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_upi_apps)
        if (upiList.size > 0) {
            recyclerView.layoutManager = GridLayoutManager(this, upiList.size)
            /*val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
            itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider, null))*/

            val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecoratorHorizontal(
                ContextCompat.getDrawable(this, R.drawable.divider)!!
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
            val adapter = UpiIntentAdapter(upiList/*getUpiAppsInstalledInDevice()*/, this)
            recyclerView.adapter = adapter
        }


        val stringBuiler = StringBuilder()
        stringBuiler.append("Phone pe is installed ${isAppInstalled(phonePe)}" + "\n")
        stringBuiler.append("Gpay is installed ${isAppInstalled(gpay)}" + "\n")
        stringBuiler.append("Paytm is installed ${isAppInstalled(payTm)}" + "\n")

        //Toast.makeText(this, stringBuiler.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return pm.getApplicationInfo(packageName, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
    }

    private fun getUpiAppsInstalledInDevice(): List<Int> {
        if (isAppInstalled(gpay)) upiList.add(R.drawable.google_pay)
        if (isAppInstalled(phonePe)) upiList.add(R.drawable.phone_pe)
        if (isAppInstalled(payTm)) upiList.add(R.drawable.paytm)
        return upiList.toList()
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}