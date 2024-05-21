package com.example.clocky.model

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clocky.R
import com.example.clocky.broadcastReceiver.AlarmBroadCastReceiver
import com.example.clocky.utilities.MyConstants
import com.example.clocky.utilities.MyToast
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Entity(tableName = "alarm_table")
class Alarm (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var enabled : Boolean,
    var operated : Boolean,
    var hour : Int,
    var minute : Int,
    var timeInMillis : Long,
    var ringtoneOn : Boolean = true,
    var vibrateOn : Boolean = true,
    var mon : Boolean, var tu : Boolean, var wed : Boolean, var th : Boolean, var fri : Boolean,
    var sat : Boolean, var sun : Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeByte(if (operated) 1 else 0)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeLong(timeInMillis)
        parcel.writeByte(if (ringtoneOn) 1 else 0)
        parcel.writeByte(if (vibrateOn) 1 else 0)
        parcel.writeByte(if (mon) 1 else 0)
        parcel.writeByte(if (tu) 1 else 0)
        parcel.writeByte(if (wed) 1 else 0)
        parcel.writeByte(if (th) 1 else 0)
        parcel.writeByte(if (fri) 1 else 0)
        parcel.writeByte(if (sat) 1 else 0)
        parcel.writeByte(if (sun) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
fun Alarm.setAlarm(context : Context)
{
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmBroadCastReceiver::class.java)
    val bundle = Bundle().apply {
        putParcelable(MyConstants.ALARM_OB, this@setAlarm)
    }
    intent.putExtra(MyConstants.BUNDLE_OB, bundle)
    intent.putExtra(MyConstants.ALARM_ID, this@setAlarm.id)

    val alarmPendingIntent = PendingIntent.getBroadcast(context, this@setAlarm.id, intent, PendingIntent.FLAG_MUTABLE)

    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@setAlarm.timeInMillis
        set(Calendar.HOUR_OF_DAY, this@setAlarm.hour)
        set(Calendar.MINUTE, this@setAlarm.minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        //if time has already passed, increment the day by 1
        if(timeInMillis <= System.currentTimeMillis())
            set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
    }

    //if it is one time alarm
    if(!recurringAlarm())
    {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmPendingIntent
        )

        var message = String.format("Alarm set for %02d:%02d %s", this.hour, this.minute, getDateTime(this.timeInMillis))
        MyToast.showToastLong(context, message)
    }
    //else if it is a recurring alarm
    else
    {
        //it is a recurring alarm, means it will repeat for atleast one week day, so this alarm will be triggered everyday
        //on the set time, and we will check if that day is true in the alarm object, if yes then we will open the ring activity
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )

        var message = String.format("Alarm set for %02d:%02d %s", this.hour, this.minute, getWeekText(this))
        MyToast.showToastLong(context, message)
    }
}

fun Alarm.cancelAlarm(context: Context)
{
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmBroadCastReceiver::class.java)
    val alarmPendingIntent = PendingIntent.getBroadcast(context, this@cancelAlarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

    alarmManager.cancel(alarmPendingIntent)

    MyToast.showToastShort(context, "Alarm cancelled!")
}

fun Alarm.recurringAlarm() : Boolean {
    return this@recurringAlarm.mon || this@recurringAlarm.tu || this@recurringAlarm.wed || this@recurringAlarm.th
        || this@recurringAlarm.fri || this@recurringAlarm.sat || this@recurringAlarm.sun
}

private fun getDateTime(timeInMillis : Long) : String{
    val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    return sdf.format(timeInMillis)
}

private fun getWeekText(alarm : Alarm) : String{
    var weekStr = ""

    if(alarm.mon) weekStr += " Mon"
    if(alarm.tu) weekStr += " Tu"
    if(alarm.wed) weekStr += " Wed"
    if(alarm.th) weekStr += " Th"
    if(alarm.fri) weekStr += " Fri"
    if(alarm.sat) weekStr += " Sat"
    if(alarm.sun) weekStr += " Sun"

    return weekStr
}