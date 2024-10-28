package com.pinelabs.pluralsdk.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.pinelabs.pluralsdk.R

class UPICollectFragment : Fragment() {

    private lateinit var etUPIId: EditText
    private lateinit var checkCircleIcon: ImageView
    private lateinit var btnVerifyContinue: Button
    private lateinit var btnBack: ImageButton

    private val UPI_REGEX = Regex("^[\\w.]{1,}-?[\\w.]{0,}-?[\\w.]{1,}@[a-zA-Z]{3,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upicollect, container, false)

        // Initialize views
        etUPIId = view.findViewById(R.id.etUPIId)
        checkCircleIcon = view.findViewById(R.id.check_circle_icon)
        btnVerifyContinue = view.findViewById(R.id.btnVerifyContinue)
        btnBack = view.findViewById(R.id.btnBack)

        // Set up back button click listener
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setupUPIIdValidation()
        return view
    }

    private fun setupUPIIdValidation() {
        etUPIId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isValidUPI = UPI_REGEX.matches(s.toString())

                // Show/hide check_circle icon based on validation
                checkCircleIcon.visibility = if (isValidUPI) View.VISIBLE else View.GONE

                // Enable/disable button and change color based on validation
                if (isValidUPI) {
                    btnVerifyContinue.isEnabled = true
                    btnVerifyContinue.setBackgroundColor(
                        resources.getColor(R.color.colorSecondary, null)
                    )
                } else {
                    btnVerifyContinue.isEnabled = false
                    btnVerifyContinue.setBackgroundColor(
                        resources.getColor(R.color.colorPrimary, null)
                    )
                }
            }
        })
    }
}
