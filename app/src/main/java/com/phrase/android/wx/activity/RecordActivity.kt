package com.phrase.android.wx.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import com.phrase.android.wx.R
import com.phrase.android.wx.WXApp
import com.phrase.android.wx.widget.CustomKeyboardView.OnKeyboardClickListener
import kotlinx.android.synthetic.main.record_activity.*
import java.math.BigDecimal
import java.util.*


class RecordActivity : BaseActivity() {

    lateinit var mf: MeasureFormat
    var currentValue: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_activity)

        mf = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.SHORT)
        mf.numberFormat.apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 2
            maximumIntegerDigits = 4
        }

        val calendar = Calendar.getInstance()
        updaDateTimePanel(calendar)
        updateValue()
        keyboard_view.setOnKeyboardListener(object : OnKeyboardClickListener {
            override fun onKeyboardDeleteClick() {
                if (currentValue.length > 1) {
                    currentValue = currentValue.substring(0, currentValue.length - 1)
                }
                updateValue()
            }

            override fun onKeyboardDigitClick(value: String) {
                val newValue = currentValue + value
                currentValue = if (newValue.toInt() > 99999) currentValue else newValue
                updateValue()
            }
        })

        timePicker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                activity.context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updaDateTimePanel(calendar)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(applicationContext)
            )
            timePickerDialog.show()
        }

        datePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(activity.context)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updaDateTimePanel(calendar)
            }
            datePickerDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (WXApp.isFirstLaunch()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        // Refresh app
        updaDateTimePanel()
        updateValue()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return true
    }

    private fun updateValue() {
        val b = BigDecimal(currentValue).movePointLeft(if (currentValue.length >= 2) 1 else 0)
        result_text.text = mf.formatMeasures(Measure(b, WXApp.getUnit()))
    }

    private fun updaDateTimePanel(c: Calendar? = null) {
        val calendar = c ?: Calendar.getInstance()
        timePicker.text = DateFormat.getTimeFormat(this).format(calendar.time)
        datePicker.text = DateFormat.getMediumDateFormat(this).format(calendar.time)
    }
}