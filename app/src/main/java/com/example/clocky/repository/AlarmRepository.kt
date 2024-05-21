package com.example.clocky.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.clocky.data.AlarmDatabase
import com.example.clocky.model.Alarm

class AlarmRepository (private val db : AlarmDatabase){

    fun getAllAlarms() : LiveData<List<Alarm>>{
        return db.getDao().getAllAlarms()
    }

    suspend fun insertAlarm(alarm : Alarm){
        db.getDao().insertAlarm(alarm)
    }

    suspend fun deleteAlarm(id : Int){
        db.getDao().deleteAlarm(id)
    }

    suspend fun updateAlarm(alarm : Alarm){
        db.getDao().updateAlarm(alarm)
    }

    suspend fun isAlarmExists(timeInMillis : Long) : Boolean{
        return db.getDao().isAlarmExists(timeInMillis)
    }

    fun getInsertedAlarm(): LiveData<Alarm> {
        // Return LiveData that observes the last inserted alarm
        return db.getDao().getLastInsertedAlarm()
    }
}