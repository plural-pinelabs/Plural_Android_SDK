package com.pinelabs.pluralsdk.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pinelabs.pluralsdk.R

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.button_test)
    }
}