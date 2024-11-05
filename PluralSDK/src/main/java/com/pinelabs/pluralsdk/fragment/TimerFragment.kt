package com.pinelabs.pluralsdk.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pinelabs.pluralsdk.R

class TimerFragment : Fragment() {

    private lateinit var circularProgressBar: ProgressBar
    private lateinit var timerTextView: TextView
    private val totalTime = 600000L
    private val interval = 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_timer, container, false)
        circularProgressBar = view.findViewById(R.id.circularProgressBar)
        timerTextView = view.findViewById(R.id.timerTextView)

        startTimer()

        return view
    }

    private fun startTimer() {
        circularProgressBar.progress = 100
        object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = String.format(
                    "%02d:%02d",
                    secondsRemaining / 60,
                    secondsRemaining % 60
                )
                val progressPercentage = (millisUntilFinished * 100 / totalTime).toInt()
                circularProgressBar.progress = progressPercentage
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                circularProgressBar.progress = 0
                // Handle timer completion
            }
        }.start()
    }
}
