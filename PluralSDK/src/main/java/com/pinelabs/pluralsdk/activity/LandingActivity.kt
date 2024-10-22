package com.pinelabs.pluralsdk.activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.fragment.PaymentOptionListing

class LandingActivity : AppCompatActivity() {

    lateinit var layoutOrginal: View
    lateinit var layoutShimmer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        getViews()
    }

    fun getViews() {
        layoutOrginal = findViewById(R.id.layout_orginal)
        layoutShimmer = findViewById(R.id.layout_shimmer)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.details_fragment, PaymentOptionListing())
        transaction.commit()

        startShimmer()
        Handler().postDelayed({
            stopShimmer()
        },2000)
    }

    fun startShimmer() {
        layoutShimmer.isVisible = true
        layoutOrginal.isVisible = false
    }

    fun stopShimmer() {
        layoutShimmer.isVisible = false
        layoutOrginal.isVisible = true
    }

}