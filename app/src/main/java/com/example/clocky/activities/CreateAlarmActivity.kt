package com.example.clocky.activities

import AlarmViewModelFactory
import android.app.DatePickerDialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.clocky.R
import com.example.clocky.data.AlarmDatabase
import com.example.clocky.databinding.ActivityCreateAlarmBinding
import com.example.clocky.model.Alarm
import com.example.clocky.model.setAlarm
import com.example.clocky.repository.AlarmRepository
import com.example.clocky.utilities.MyToast
import com.example.clocky.utilities.StatusBarManager
import com.example.clocky.viewmodel.AlarmViewModel
import java.nio.channels.CancelledKeyException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateAlarmActivity : AppCompatActivity() {
    private var bind: ActivityCreateAlarmBinding? = null
    private lateinit var alarmViewModel: AlarmViewModel

    private var title: String = "Alarm"
    private var enabled: Boolean = false
    private var hour: Int = 0
    private var minute: Int = 0
    private var timeInMillis: Long = 0L
    private var ringtoneOn: Boolean = true
    private var vibrateOn: Boolean = true
    private var mon: Boolean = false
    private var tu: Boolean = false
    private var wed: Boolean = false
    private var th: Boolean = false
    private var fri: Boolean = false
    private var sat: Boolean = false
    private var sun: Boolean = false

    private var dateTimeMillis: Long = getTodayDateInMillis()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.bg_color)

        bind = ActivityCreateAlarmBinding.inflate(layoutInflater)
        setContentView(bind!!.root)

        setupViewModel()
        font_init()
        alarmDateInit()
        setupListeners()
    }

    private fun setupViewModel() {
        val noteRepository = AlarmRepository(AlarmDatabase(this))
        val viewModelProviderFactory = AlarmViewModelFactory(application, noteRepository)
        alarmViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[AlarmViewModel::class.java] // [] here, same as get()
    }

    private fun font_init() {
        var typeface = ResourcesCompat.getFont(this, R.font.bitsumis_font)
        bind!!.numHourPicker.setTypeface(typeface)
        bind!!.numMinutePicker.setTypeface(typeface)
        bind!!.numHourPicker.setSelectedTypeface(typeface)
        bind!!.numMinutePicker.setSelectedTypeface(typeface)
    }

    private fun alarmDateInit() {
        setAlarmDate(dateTimeMillis)
    }

    private fun setupListeners() {
        bind!!.numHourPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = newVal
            setAlarmDate(dateTimeMillis)
        }

        bind!!.numMinutePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            minute = newVal
            setAlarmDate(dateTimeMillis)
        }

        bind!!.btnCalendar.setOnClickListener {
            showDatePickerDialog()
        }

        bind!!.btnMon.setOnClickListener {
            mon = !mon
            if (mon) {
                bind!!.btnMon.setShapeType(1)
                bind!!.btnMon.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnMon.setShapeType(0)
                bind!!.btnMon.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnTu.setOnClickListener {
            tu = !tu
            if (tu) {
                bind!!.btnTu.setShapeType(1)
                bind!!.btnTu.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnTu.setShapeType(0)
                bind!!.btnTu.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnWed.setOnClickListener {
            wed = !wed
            if (wed) {
                bind!!.btnWed.setShapeType(1)
                bind!!.btnWed.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnWed.setShapeType(0)
                bind!!.btnWed.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnTh.setOnClickListener {
            th = !th
            if (th) {
                bind!!.btnTh.setShapeType(1)
                bind!!.btnTh.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnTh.setShapeType(0)
                bind!!.btnTh.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnFri.setOnClickListener {
            fri = !fri
            if (fri) {
                bind!!.btnFri.setShapeType(1)
                bind!!.btnFri.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnFri.setShapeType(0)
                bind!!.btnFri.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnSat.setOnClickListener {
            sat = !sat
            if (sat) {
                bind!!.btnSat.setShapeType(1)
                bind!!.btnSat.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnSat.setShapeType(0)
                bind!!.btnSat.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.btnSun.setOnClickListener {
            sun = !sun
            if (sun) {
                bind!!.btnSun.setShapeType(1)
                bind!!.btnSun.setTextColor(ContextCompat.getColor(this, R.color.theme_color))
            } else {
                bind!!.btnSun.setShapeType(0)
                bind!!.btnSun.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color_secondary
                    )
                )
            }
        }

        bind!!.switchRingtone.setOnCheckedChangeListener { buttonView, isChecked ->
            ringtoneOn = isChecked
        }

        bind!!.switchVibrate.setOnCheckedChangeListener { buttonView, isChecked ->
            vibrateOn = isChecked
        }

        bind!!.btnDone.setOnClickListener {
            finalize()
        }

        bind!!.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun finalize(){
        //checking last if the set alarm time is not past
        if (calendar.timeInMillis <= System.currentTimeMillis())
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)

        //checking if the alarm is duplicate
        alarmViewModel.isAlarmExists(calendar.timeInMillis).observe(this, Observer { isAlarmExists ->
            if (isAlarmExists) {
                MyToast.showToastShort(this, "Alarm already exists")
            } else {
                enabled = true

                if (!bind!!.alarmTitle.text.isNullOrEmpty() && !bind!!.alarmTitle.text.isNullOrBlank())
                    title = bind!!.alarmTitle.text.toString()

                timeInMillis = calendar.timeInMillis

                val alarmOb = Alarm(
                    0,
                    title,
                    enabled,
                    operated = false,
                    hour,
                    minute,
                    timeInMillis,
                    ringtoneOn,
                    vibrateOn,
                    mon, tu, wed, th, fri, sat, sun
                )

                alarmViewModel.insertAlarm(alarmOb)
                finish()
            }
        })
    }

    private fun setAlarmDate(timeInMillis_: Long) {
        calendar.timeInMillis = timeInMillis_
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //if time is past, setting time for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
            bind!!.alarmDate.text = "Tomorrow - " + getDate(calendar.timeInMillis)
        } else if (isDateTomorrow())
            bind!!.alarmDate.text = "Tomorrow - " + getDate(calendar.timeInMillis)
        else if (isDateToday())
            bind!!.alarmDate.text = "Today - " + getDate(calendar.timeInMillis)
        else
            bind!!.alarmDate.text = getDate(calendar.timeInMillis)
    }

    private fun getDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        return sdf.format(timeInMillis)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, R.style.my_dialog_theme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Convert selected date to milliseconds
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, monthOfYear, dayOfMonth)
                val selectedDateInMillis = selectedCalendar.timeInMillis

                dateTimeMillis = selectedDateInMillis

                setAlarmDate(dateTimeMillis)

                // Now you have the selected date in milliseconds
                // You can use it as needed
            }, year, month, dayOfMonth
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        //setting calendar start date to be the previous selected date
        val previousSelectedDateCalendar = Calendar.getInstance()
        previousSelectedDateCalendar.timeInMillis = dateTimeMillis
        datePickerDialog.updateDate(
            previousSelectedDateCalendar.get(Calendar.YEAR),
            previousSelectedDateCalendar.get(Calendar.MONTH),
            previousSelectedDateCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.window?.setBackgroundDrawableResource(R.drawable.date_picker_drawable)
        datePickerDialog.show()
    }

    private fun getTodayDateInMillis(): Long {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        return today.timeInMillis
    }

    private fun isDateToday(): Boolean {
        val today = Calendar.getInstance() // Get the current date
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun isDateTomorrow(): Boolean {
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1) // Adding one day to get tomorrow's date

        return calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH)
    }

    override fun onDestroy() {
        bind = null
        super.onDestroy()
    }
}
