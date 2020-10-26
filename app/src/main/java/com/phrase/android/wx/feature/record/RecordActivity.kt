package com.phrase.android.wx.feature.record

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.phrase.android.wx.R
import com.phrase.android.wx.WXApp
import com.phrase.android.wx.core.WorkoutRepository
import com.phrase.android.wx.utils.DateTimeUtils
import com.phrase.android.wx.widget.BaseActivity
import com.phrase.android.wx.widget.CustomKeyboardView.OnKeyboardClickListener
import kotlinx.android.synthetic.main.activity_record.*
import java.math.BigDecimal
import java.util.*


class RecordActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, RecordActivity::class.java))
        }
    }

    lateinit var mf: MeasureFormat
    var currentValue: String = "0"
    var value: BigDecimal = BigDecimal.valueOf(0)
    var calendar: Calendar = Calendar.getInstance()
    var selectedWorkout: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        button_negative.setOnClickListener { finish() }
        button_positive.setOnClickListener {
            WorkoutRepository.getInstance(this).addWorkout(
                selectedWorkout,
                value.toDouble(),
                calendar
            )
            Toast.makeText(this, R.string.workout_added, Toast.LENGTH_SHORT).show()
            finish()
        }

        mf = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.SHORT)
        mf.numberFormat.apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 2
            maximumIntegerDigits = 4
        }

        calendar = Calendar.getInstance()
        updateDateTimePanel()
        updateValue()

        ArrayAdapter.createFromResource(
            this,
            R.array.workout_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_activity.adapter = adapter
        }
        spinner_activity.onItemSelectedListener = this

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
                timePicker.context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateDateTimePanel()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(applicationContext)
            )
            timePickerDialog.show()
        }

        datePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(datePicker.context)
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateTimePanel()
            }
            datePickerDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh app
        updateDateTimePanel()
        updateValue()
    }

    private fun updateValue() {
        value = BigDecimal(currentValue).movePointLeft(if (currentValue.length >= 2) 1 else 0)
        result_text.text = mf.formatMeasures(Measure(value, WXApp.getUnit()))
        // Output: 2.5 kg (en-US) | 2.5 kg (en-GB) | 2.5 kg (es-419) | 2,5 kg (es)
        // Output: 2.5 lb (en-US) | 2.5 lb (en-GB) | 2.5 lb (es-419) | 2,5 lb (es)
    }

    private fun updateDateTimePanel(isDefault: Boolean = true) {
        timePicker.text = DateTimeUtils.getTimeFormat(this, Date(calendar.timeInMillis), isDefault)
        datePicker.text = DateTimeUtils.getDateFormat(this, Date(calendar.timeInMillis), isDefault)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedWorkout = position
    }
}