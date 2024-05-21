package com.example.clocky.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.clocky.model.Alarm
import com.example.clocky.utilities.MyConstants

@Database (entities = [Alarm::class], version = 1)
abstract class AlarmDatabase : RoomDatabase(){

    abstract fun getDao() : AlarmDao

    companion object{

        @Volatile
        private var Instance : AlarmDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) : AlarmDatabase
        {
            return Instance ?: synchronized(LOCK){
                createDatabase(context).also{
                    Instance = it
                }
            }
        }

        private fun createDatabase(context: Context) : AlarmDatabase
        {
            return Room.databaseBuilder(
                context.applicationContext,
                AlarmDatabase::class.java,
                MyConstants.DB_NAME
            ).build()
        }
    }
}