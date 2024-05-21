package com.example.clocky.activities

import AlarmViewModelFactory
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.clocky.R
import com.example.clocky.data.AlarmDatabase
import com.example.clocky.databinding.ActivityRingBinding
import com.example.clocky.model.Alarm
import com.example.clocky.model.cancelAlarm
import com.example.clocky.model.setAlarm
import com.example.clocky.repository.AlarmRepository
import com.example.clocky.services.AlarmService
import com.example.clocky.utilities.MyConstants
import com.example.clocky.utilities.StatusBarManager
import com.example.clocky.viewmodel.AlarmViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class RingActivity : AppCompatActivity() {
    private var bind : ActivityRingBinding? = null
    private lateinit var alarmViewModel : AlarmViewModel
    private lateinit var alarm : Alarm
    private var alarmId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.bg_color)

        bind = ActivityRingBinding.inflate(layoutInflater)
        setContentView(bind!!.root)

        setupViewModel()
        dataInit()
        setupListeners()
    }

    private fun setupViewModel() {
        val alarmRepository = AlarmRepository(AlarmDatabase(this))
        val viewModelProviderFactory = AlarmViewModelFactory(application, alarmRepository)
        alarmViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[AlarmViewModel::class.java] // [] here, same as get()
    }

    private fun dataInit(){
        val bundle = intent.getBundleExtra(MyConstants.BUNDLE_OB)
        alarm = bundle?.getParcelable<Alarm>(MyConstants.ALARM_OB) as Alarm
        alarmId = intent.getIntExtra(MyConstants.ALARM_ID, 0)

        bind!!.alarmTime.text = String.format("%02d:%02d", alarm.hour, alarm.minute)
        bind!!.alarmDate.text = getDateString(alarm.timeInMillis)

    }

    private fun setupListeners(){
        bind!!.btnClose.setOnClickListener{
            if(!recurring()){
                alarm.enabled = false
                var newAlarmOb = getAlarmOb(alarm)
                alarmViewModel.updateAlarm(newAlarmOb)
            }

            val serviceIntent = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(serviceIntent)
            finish()
        }
    }

    private fun getAlarmOb(alarm : Alarm) : Alarm{
        return Alarm(alarmId, alarm.title, alarm.enabled, true, alarm.hour, alarm.minute, alarm.timeInMillis, alarm.ringtoneOn,
            alarm.vibrateOn, alarm.mon, alarm.tu, alarm.wed, alarm.th, alarm.fri, alarm.sat, alarm.sun)
    }

    private fun getDateString(timeInMillis : Long) : String{
        val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        return sdf.format(timeInMillis)
    }

    private fun recurring() : Boolean{
        return alarm.mon || alarm.tu || alarm.wed || alarm.th || alarm.fri || alarm.sat || alarm.sun
    }

    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }
}