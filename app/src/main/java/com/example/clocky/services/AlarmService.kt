package com.example.clocky.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.clocky.R
import com.example.clocky.activities.RingActivity
import com.example.clocky.model.Alarm
import com.example.clocky.model.setAlarm
import com.example.clocky.utilities.MyConstants

class AlarmService : Service() {
    private lateinit var alarm: Alarm
    private var vibrator: Vibrator? = null
    private var alarmId : Int = 0
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate() {
        super.onCreate()

        var ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm_sound)
        mediaPlayer = MediaPlayer.create(this, ringtoneUri)
        mediaPlayer.isLooping = true
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.getBundleExtra(MyConstants.BUNDLE_OB)
        alarm = bundle?.getParcelable<Alarm>(MyConstants.ALARM_OB) as Alarm
        alarmId = intent.getIntExtra(MyConstants.ALARM_ID, 0)

        val notificationIntent = Intent(this, RingActivity::class.java).apply {
            putExtra(MyConstants.BUNDLE_OB, bundle)
            putExtra(MyConstants.ALARM_ID, alarmId)
        }

        playMediaAndVibrate()

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val mNotificationManager = NotificationCompat.Builder(this, "2")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.apply {
            setContentTitle("Alarm for ${String.format("%02d", alarm.hour)}:${String.format("%02d", alarm.minute)}")
            setContentText("Ringing...")
            setSmallIcon(R.drawable.clocky_icon)
            setProgress(100, 0, true)
            setContentIntent(pendingIntent)
            setOngoing(true)
            setAutoCancel(true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MyConstants.CHANNEL_ID,
                "Alarm notification channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(channel)
            mNotificationManager.setChannelId(MyConstants.CHANNEL_ID)
        }

        val notification = mNotificationManager.build()
        startForeground(2, notification)

        return START_STICKY
    }

    private fun playMediaAndVibrate() {
        if (alarm.ringtoneOn) {
            mediaPlayer.start()
        }

        if (alarm.vibrateOn) {
            val pattern = longArrayOf(0, 300, 1200)
            vibrator?.vibrate(pattern, 0)
        }
    }

    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        vibrator?.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}