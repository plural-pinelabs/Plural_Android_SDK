package com.pinelabs.pluralsdk.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.pinelabs.pluralsdk.R
import com.pinelabs.pluralsdk.activity.ACSPageActivity
import com.pinelabs.pluralsdk.utils.Constants.Companion.ORDER_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.PAYMENT_ID
import com.pinelabs.pluralsdk.utils.Constants.Companion.REDIRECT_URL
import java.util.Timer

class OtpFragment : Fragment() {

    private lateinit var textResendOtp: TextView
    private lateinit var resendOtpTimer: LinearLayout
    private lateinit var textTimer: TextView
    private lateinit var txtAutoRead: LinearLayout
    private lateinit var btnVerifyContinue: Button
    private lateinit var linearAcsPage: LinearLayout

    var t = Timer()
    private var countDownTimer: CountDownTimer? = null

    private val totalTime = 600000L
    private val interval = 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textResendOtp = view.findViewById(R.id.resend_otp_text)
        resendOtpTimer = view.findViewById(R.id.linear_resend_otp_timer)
        textTimer = view.findViewById(R.id.resend_otp_timer)
        txtAutoRead = view.findViewById(R.id.linear_auto_read)
        btnVerifyContinue = view.findViewById(R.id.btnProceedToPay)
        btnVerifyContinue.background = buttonBackground(requireActivity())
        btnVerifyContinue.isEnabled = false
        btnVerifyContinue.alpha = 0.3f
        linearAcsPage = view.findViewById(R.id.linear_bank)
        linearAcsPage.setOnClickListener {
            val i = Intent(activity, ACSPageActivity::class.java)
            i.putExtra(REDIRECT_URL, "")
            i.putExtra(ORDER_ID, "")
            i.putExtra(PAYMENT_ID, "")
            startActivity(i)
            requireActivity().finish()
        }

        textResendOtp.setOnClickListener {
            resendTimer(true)
        }

    }

    fun resendTimer(visible: Boolean) {
        textResendOtp.visibility = View.GONE
        resendOtpTimer.visibility = View.VISIBLE
        txtAutoRead.visibility = View.VISIBLE
        startTimer()
    }

    private fun startTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                textTimer.text = String.format(
                    "%02d:%02d",
                    secondsRemaining / 60,
                    secondsRemaining % 60
                )
            }

            override fun onFinish() {
                textTimer.text = "00:00"
            }
        }.start()
    }

    public fun buttonBackground(context: Context): Drawable {

        val stateListDrawable = StateListDrawable()

        // Create different drawables for different states
        val pressedDrawable = GradientDrawable().apply {
            /*if (palette != null) {
                setColor(Color.parseColor(palette?.C900))
            } else {*/
            setColor(context.resources.getColor(R.color.header_color))
            /*}*/
            cornerRadius = 16f // Normal corner radius
        }

        // Add states to the StateListDrawable
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), pressedDrawable) // Default state

        return stateListDrawable
    }

}