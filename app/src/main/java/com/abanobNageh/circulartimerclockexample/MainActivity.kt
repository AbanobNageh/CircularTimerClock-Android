package com.abanobNageh.circulartimerclockexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import abanobNageh.com.circulartimerclock.CircularTimerClock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clock = findViewById<View>(R.id.circular_clock) as CircularTimerClock
        val startHourEditText = findViewById<View>(R.id.start_hour_edit_text) as EditText
        val endHourEditText = findViewById<View>(R.id.end_hour_edit_text) as EditText
        val startMinuteEditText = findViewById<View>(R.id.start_minute_edit_text) as EditText
        val endMinuteEditText = findViewById<View>(R.id.end_minute_edit_text) as EditText
        val setStartTimeButton = findViewById<View>(R.id.set_start_time_button) as Button
        val setEndTimeButton = findViewById<View>(R.id.set_end_time_button) as Button

        clock.setOnTimeChangedListener(object : CircularTimerClock.ontTimeChanged {
            override fun onStartTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean) {
                Log.d("start time: ", "" + time)
            }

            override fun onEndTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean) {
                Log.d("end time: ", "" + time)
            }
        })

        setStartTimeButton.setOnClickListener {
            val startHour: String = startHourEditText.text.toString()
            val startMinute: String = startMinuteEditText.text.toString()

            if (startHour == "" || startMinute == "") {
                return@setOnClickListener
            }

            clock.setStartTime(startHour.toInt(), startMinute.toInt())
        }

        setEndTimeButton.setOnClickListener {
            val endHour: String = endHourEditText.text.toString()
            val endMinute: String = endMinuteEditText.text.toString()

            if (endHour == "" || endMinute == "") {
                return@setOnClickListener
            }

            clock.setEndTime(endHour.toInt(), endMinute.toInt())
        }
    }
}