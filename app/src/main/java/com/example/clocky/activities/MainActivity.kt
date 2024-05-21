package com.example.clocky.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.clocky.R
import com.example.clocky.databinding.ActivityMainBinding
import com.example.clocky.fragments.AlarmFragment
import com.example.clocky.fragments.ClockFragment
import com.example.clocky.fragments.StopwatchFragment
import com.example.clocky.fragments.TimerFragment
import com.example.clocky.utilities.StatusBarManager

class MainActivity : AppCompatActivity() {
    private var bind: ActivityMainBinding? = null
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.bg_color)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind!!.root)

        //default fragment
        replaceFragment(ClockFragment())

        setButtonListeners()
    }

    private fun setButtonListeners() {
        bind!!.btnClock.setOnClickListener {
            if (currentFragment !is ClockFragment)
                replaceFragment(ClockFragment())
        }

        bind!!.btnAlarm.setOnClickListener {
            if (currentFragment !is AlarmFragment)
                replaceFragment(AlarmFragment())
        }

        bind!!.btnStopwatch.setOnClickListener {
            if (currentFragment !is StopwatchFragment)
                replaceFragment(StopwatchFragment())
        }

        bind!!.btnTimer.setOnClickListener {
            if (currentFragment !is TimerFragment)
                replaceFragment(TimerFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        currentFragment = fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(bind!!.frameLayout.id, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        bind = null
        super.onDestroy()
    }
}