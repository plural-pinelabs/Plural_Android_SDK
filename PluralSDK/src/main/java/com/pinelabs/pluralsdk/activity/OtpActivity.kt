package com.pinelabs.pluralsdk.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.pinelabs.pluralsdk.R


class OtpActivity : Activity() {

    lateinit var edt1: EditText
    lateinit var edt2: EditText
    lateinit var edt3: EditText
    lateinit var edt4: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_layout)

        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);

        edt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (TextUtils.isEmpty(edt1.text.toString().trim { it <= ' ' })) {
                    edt1.setBackgroundResource(R.drawable.otp_background)
                    edt1.requestFocus()
                    edt1.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.black))
                    Handler().postDelayed(
                        Runnable { edt1.inputType = InputType.TYPE_CLASS_TEXT },
                        500
                    )
                } else {
                    edt1.setBackgroundResource(R.drawable.otp_filled)
                    edt1.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.red))

                    edt2.requestFocus()
                    Handler().postDelayed(Runnable {
                        edt1.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }, 500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        edt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (TextUtils.isEmpty(edt2.text.toString().trim { it <= ' ' })) {
                    edt2.setBackgroundResource(R.drawable.otp_background)
                    edt2.requestFocus()
                    edt2.setTextColor(
                        ContextCompat.getColor(
                            this@OtpActivity,
                            com.pinelabs.pluralsdk.R.color.black
                        )
                    )
                    Handler().postDelayed({ edt2.inputType = InputType.TYPE_CLASS_TEXT }, 500)
                } else {
                    edt2.setBackgroundResource(R.drawable.otp_filled)
                    edt3.requestFocus()
                    edt2.setTextColor(
                        ContextCompat.getColor(
                            this@OtpActivity,
                            com.pinelabs.pluralsdk.R.color.red
                        )
                    )
                    Handler().postDelayed({
                        edt2.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }, 500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        edt3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (TextUtils.isEmpty(edt3.text.toString().trim { it <= ' ' })) {
                    edt3.setBackgroundResource(R.drawable.otp_background)
                    edt3.requestFocus()
                    edt3.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.black))
                    Handler().postDelayed({ edt3.inputType = InputType.TYPE_CLASS_TEXT }, 500)
                } else {
                    edt3.setBackgroundResource(R.drawable.otp_filled)
                    edt3.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.red))
                    edt4.requestFocus()
                    Handler().postDelayed({
                        edt3.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }, 500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        edt4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (TextUtils.isEmpty(edt4.text.toString().trim { it <= ' ' })) {
                    edt4.setBackgroundResource(R.drawable.otp_background)
                    edt4.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.black))
                    Handler().postDelayed({ edt4.inputType = InputType.TYPE_CLASS_TEXT }, 500)
                } else {
                    edt4.setBackgroundResource(R.drawable.otp_filled)
                    edt4.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.red))
                    edt4.clearFocus()
                    Handler().postDelayed({
                        edt4.inputType = InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }, 500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

    }
}