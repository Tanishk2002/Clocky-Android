package com.example.clocky.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.clocky.model.Alarm

@Dao
interface AlarmDao {

    @Query("Select * from alarm_table")
    fun getAllAlarms() : LiveData<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm : Alarm)

    @Query("Delete from alarm_table where id=:id")
    suspend fun deleteAlarm(id : Int)

    @Update
    suspend fun updateAlarm(alarm : Alarm)

    @Query("Select exists (Select 1 from alarm_table where timeInMillis = :timeInMillis_ Limit 1)")
    suspend fun isAlarmExists(timeInMillis_ : Long) : Boolean

    @Query("SELECT * FROM alarm_table ORDER BY id DESC LIMIT 1")
    fun getLastInsertedAlarm(): LiveData<Alarm>
}