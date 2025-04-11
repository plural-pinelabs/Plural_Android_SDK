package com.pinelabs.pluralsdk.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecoratorHorizontal
import com.pinelabs.pluralsdk.adapter.UpiIntentAdapter
import com.pinelabs.pluralsdk.utils.Constants.Companion.GPAY
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYTM
import com.pinelabs.pluralsdk.utils.Constants.Companion.PHONEPE
import com.pinelabs.pluralsdk.utils.Constants.Companion.UPI_INTENT_PREFIX

class BottomSheetRetryUpiFragment : BottomSheetDialogFragment(),
    UpiIntentAdapter.OnItemClickListener {

    private lateinit var recyclerPBPApps: RecyclerView
    val upiList = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.retry_upi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerPBPApps = view.findViewById(R.id.recycler_upi_apps)

        getUpiAppsInstalledInDevice()
        if (upiList.size == 0) {
            recyclerPBPApps.visibility = View.GONE
        } else {
            recyclerPBPApps.layoutManager = GridLayoutManager(requireActivity(), upiList.size)

            val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecoratorHorizontal(
                ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!
            )
            recyclerPBPApps.addItemDecoration(dividerItemDecoration)
            val adapter = UpiIntentAdapter(upiList, this)
            recyclerPBPApps.adapter = adapter
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), upiList.get(position), Toast.LENGTH_SHORT).show()
    }

     fun getUpiAppsInstalledInDevice(): List<Int> {
        if (isAppInstalled(GPAY) && isAppUpiReady(GPAY)) upiList.add(R.drawable.google_pay)
        if (isAppInstalled(PHONEPE) && isAppUpiReady(PHONEPE)) upiList.add(R.drawable.phone_pe)
        if (isAppInstalled(PAYTM) && isAppUpiReady(PAYTM)) upiList.add(R.drawable.paytm)
        return upiList.toList()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = requireActivity().packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return pm.getApplicationInfo(packageName, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
    }

    fun isAppUpiReady(packageName: String): Boolean {
        var appUpiReady = false
        val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse(UPI_INTENT_PREFIX))
        val pm = requireActivity().packageManager
        val upiActivities: List<ResolveInfo> = pm.queryIntentActivities(upiIntent, 0)
        for (a in upiActivities) {
            if (a.activityInfo.packageName == packageName) appUpiReady = true
        }
        return appUpiReady
    }

}