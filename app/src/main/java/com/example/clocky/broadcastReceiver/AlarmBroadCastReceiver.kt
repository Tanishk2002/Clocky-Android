package com.example.clocky.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import com.example.clocky.R
import com.example.clocky.activities.RingActivity
import com.example.clocky.model.Alarm
import com.example.clocky.model.setAlarm
import com.example.clocky.services.AlarmService
import com.example.clocky.utilities.MyConstants
import com.example.clocky.utilities.MyToast
import java.util.Calendar

class AlarmBroadCastReceiver : BroadcastReceiver() {
    private lateinit var alarm : Alarm
    private var alarmId : Int = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        MyToast.showToastShort(context!!, "Alarm triggered!")

        val bundle = intent?.getBundleExtra(MyConstants.BUNDLE_OB)

        if(bundle != null) {
            alarm = bundle.getParcelable<Alarm>(MyConstants.ALARM_OB) as Alarm
            alarmId = intent.getIntExtra(MyConstants.ALARM_ID, 0)

            if(!recurring(alarm))
                startAlarmService(context, alarm)
            else if(isAlarmToday(alarm))
                startAlarmService(context, alarm)
        }
    }

    private fun startAlarmService(context: Context?, alarm: Alarm){
        val serviceIntent = Intent(context, AlarmService::class.java)
        val bundle = Bundle().apply {
            putParcelable(MyConstants.ALARM_OB, alarm)
        }
        serviceIntent.putExtra(MyConstants.BUNDLE_OB, bundle)
        serviceIntent.putExtra(MyConstants.ALARM_ID, alarmId)
        context?.startForegroundService(serviceIntent)
    }

    private fun recurring(alarm: Alarm) : Boolean{
        return alarm.mon || alarm.tu || alarm.wed || alarm.th || alarm.fri || alarm.sat || alarm.sun
    }

    private fun isAlarmToday(alarm: Alarm) : Boolean{
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        when (dayOfWeek) {
            Calendar.SUNDAY -> return alarm.sun
            Calendar.MONDAY -> return alarm.mon
            Calendar.TUESDAY -> return alarm.tu
            Calendar.WEDNESDAY -> return alarm.wed
            Calendar.THURSDAY -> return alarm.th
            Calendar.FRIDAY -> return alarm.fri
            Calendar.SATURDAY -> return alarm.sat
        }
        return false
    }
}