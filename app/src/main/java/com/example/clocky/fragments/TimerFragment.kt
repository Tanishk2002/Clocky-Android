package com.example.clocky.fragments

import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.example.clocky.R
import com.example.clocky.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    private var bind : FragmentTimerBinding? = null
    private var timeInSeconds = 0
    private var hour = 0
    private var minute = 0
    private var sec = 0
    private var started = false

    private var handler = Handler(Looper.getMainLooper())
    private var runnable = object : Runnable{
        override fun run() {
            if(timeInSeconds == 0)
                bind!!.btnReset.performClick()

            timeInSeconds -= 1

            bind!!.tvTime.text = String.format("%02d:%02d:%02d", (timeInSeconds / 3600), (timeInSeconds / 60),
                (timeInSeconds % 3600) % 60)

            if(timeInSeconds > -1)
              handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentTimerBinding.inflate(inflater, container, false)
        return bind!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        font_init()
        setupProperties()
        setupListeners()
    }

    private fun font_init() {
        var typeface = ResourcesCompat.getFont(requireContext(), R.font.bitsumis_font)
        bind!!.numHourPicker.setTypeface(typeface)
        bind!!.numMinutePicker.setTypeface(typeface)
        bind!!.numSecondPicker.setTypeface(typeface)
        bind!!.numHourPicker.setSelectedTypeface(typeface)
        bind!!.numMinutePicker.setSelectedTypeface(typeface)
        bind!!.numSecondPicker.setSelectedTypeface(typeface)
    }

    private fun setupProperties(){

    }

    private fun setupListeners(){
        bind!!.numHourPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = newVal
        }

        bind!!.numMinutePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            minute = newVal
        }

        bind!!.numSecondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            sec = newVal
        }

        bind!!.btnStart.setOnClickListener {
            if(hour > 0 || minute > 0 || sec > 0){
                bind!!.selectTime.visibility = View.GONE
                bind!!.btnStart.visibility = View.GONE

                bind!!.timer.visibility = View.VISIBLE
                bind!!.timerButtons.visibility = View.VISIBLE

                initializeTimer()
            }
        }

        bind!!.btnPlayPause.setOnClickListener{
            started = !started

            if(started) {
                bind!!.imageAvd.setImageResource(R.drawable.play_to_pause_avd)
                val drawable = bind!!.imageAvd.drawable
                if(drawable is AnimatedVectorDrawable)
                    drawable.start()

                bind!!.circularBars.visibility = View.VISIBLE
                bind!!.arcs.visibility = View.VISIBLE

                bind!!.circularBars.startAnimation()
                bind!!.arcs.startAnimation()

                handler.postDelayed(runnable, 0)
            }
            else {
                bind!!.imageAvd.setImageResource(R.drawable.pause_to_play_avd)
                val drawable = bind!!.imageAvd.drawable
                if(drawable is AnimatedVectorDrawable)
                    drawable.start()

                bind!!.circularBars.visibility = View.GONE
                bind!!.arcs.visibility = View.GONE

                bind!!.circularBars.stopAnimation()
                bind!!.arcs.stopAnimation()

                handler.removeCallbacks(runnable)
            }
        }

        bind!!.btnReset.setOnClickListener{
            bind!!.timer.visibility = View.GONE
            bind!!.timerButtons.visibility = View.GONE

            bind!!.selectTime.visibility = View.VISIBLE
            bind!!.btnStart.visibility = View.VISIBLE

            bind!!.imageAvd.setImageResource(R.drawable.play_to_pause_avd)

            handler.removeCallbacks(runnable)
            started = false
        }
    }

    private fun initializeTimer(){
        bind!!.arcs.visibility = View.GONE
        bind!!.circularBars.visibility = View.GONE

        bind!!.tvTime.text = String.format("%02d:%02d:%02d", hour, minute, sec)

        timeInSeconds = (hour * 60 * 60) + (minute * 60) + sec
    }

    override fun onDestroy() {
        super.onDestroy()
        bind = null
        handler.removeCallbacks(runnable)
    }
}