package com.example.clocky.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.clocky.model.Alarm
import com.example.clocky.repository.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application, private val alarmRepository: AlarmRepository) :
    AndroidViewModel(application) {

    fun getAllAlarms() = alarmRepository.getAllAlarms()
    
    fun insertAlarm(alarm : Alarm) = viewModelScope.launch {
        alarmRepository.insertAlarm(alarm)
    }

    fun deleteAlarm(id: Int) = viewModelScope.launch {
        alarmRepository.deleteAlarm(id)
    }

    fun updateAlarm(alarm : Alarm) = viewModelScope.launch {
        alarmRepository.updateAlarm(alarm)
    }

    fun isAlarmExists(timeInMillis : Long): LiveData<Boolean> {
        return liveData {
            val result = alarmRepository.isAlarmExists(timeInMillis)
            emit(result)
        }
    }

    fun getInsertedAlarm(): LiveData<Alarm> {
        return alarmRepository.getInsertedAlarm()
    }
}