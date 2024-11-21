package com.pinelabs.pluralsdk.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.adapter.DividerItemDecorator
import com.pinelabs.pluralsdk.adapter.GridDividerItemDecoration
import com.pinelabs.pluralsdk.adapter.NetBankAllAdapter
import com.pinelabs.pluralsdk.adapter.NetBanksAdapter
import com.pinelabs.pluralsdk.data.model.PBPBank
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_AXIS
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_CITI
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_HDFC
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_ICICI
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_KOTAK
import com.pinelabs.pluralsdk.utils.Constants.Companion.BANK_SBI

class NetBankingFragment : Activity/*Fragment*/(), NetBankAllAdapter.OnItemClickListener {

    private lateinit var recyclerNetBanks: RecyclerView
    private lateinit var linearMoreBanks: LinearLayout

    /* override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         return inflater.inflate(R.layout.netbanking_landing, container, false)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         recyclerNetBanks = view.findViewById(R.id.recycler_net_banks)
         recyclerNetBanks.layoutManager = GridLayoutManager(requireActivity(), 3)

         val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecoratorHorizontal(
             ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!
         )

         recyclerNetBanks.addItemDecoration(dividerItemDecoration)
         val adapter = PBPBanksAdapter(getBankList())
         recyclerNetBanks.adapter = adapter
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.netbanking_landing)

        linearMoreBanks = findViewById(R.id.linear_more_banks)
        recyclerNetBanks = findViewById(R.id.recycler_net_banks)
        recyclerNetBanks.layoutManager = GridLayoutManager(this, 3)

        linearMoreBanks.setOnClickListener {
            showMoreBanks()
        }

        val dividerItemDecoration: RecyclerView.ItemDecoration =
            GridDividerItemDecoration(this, GridDividerItemDecoration.ALL)

        recyclerNetBanks.addItemDecoration(
            dividerItemDecoration
        )

        val adapter = NetBanksAdapter(getBankList())
        recyclerNetBanks.adapter = adapter
    }

    fun getBankList(): List<PBPBank> {
        val pbpBankList = ArrayList<PBPBank>()
        pbpBankList.add(PBPBank(BANK_HDFC, R.drawable.hdfc_bank))
        pbpBankList.add(PBPBank(BANK_SBI, R.drawable.state_bank_of_india))
        pbpBankList.add(PBPBank(BANK_ICICI, R.drawable.icici_bank))
        pbpBankList.add(PBPBank(BANK_AXIS, R.drawable.axis_bank))
        pbpBankList.add(PBPBank(BANK_KOTAK, R.drawable.kotak_bank))
        pbpBankList.add(PBPBank(BANK_CITI, R.drawable.citi_bank))
        return pbpBankList.toList()
    }

    private fun showMoreBanks() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view =
            LayoutInflater.from(this).inflate(R.layout.netbanking_all_banks, null)
        bottomSheetDialog.setContentView(view)

        val btnNo: ImageView = view.findViewById(R.id.img_close)
        val recyclerAllBanks: RecyclerView = view.findViewById(R.id.recycler_net_all_banks)
        val layoutManager =
            LinearLayoutManager(this@NetBankingFragment, LinearLayoutManager.VERTICAL, false)
        val myRecyclerViewAdapter = NetBankAllAdapter(getBankList(),this@NetBankingFragment)
        recyclerAllBanks.adapter = myRecyclerViewAdapter
        recyclerAllBanks.layoutManager = layoutManager
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(
            ContextCompat.getDrawable(this, R.drawable.divider)!!
        )
        recyclerAllBanks.addItemDecoration(dividerItemDecoration)
        myRecyclerViewAdapter.notifyDataSetChanged()

        btnNo.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onItemClick(item: PBPBank?) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }

}