package com.pinelabs.pluralsdk.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.R


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.button_progress)

        val progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar

        val objectAnimator = ObjectAnimator.ofInt(
            progressBar, "progress",
            progressBar.progress, 100
        ).setDuration(2000)

        objectAnimator.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            progressBar.progress = progress
        }

        val btn = findViewById<View>(R.id.btnProceedToPay) as TextView
        btn.setOnClickListener { objectAnimator.start() }
    }
}