package com.example.clocky.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clocky.R
import com.example.clocky.databinding.FragmentStopwatchBinding

class StopwatchFragment : Fragment() {
    private var bind : FragmentStopwatchBinding? = null
    private var started : Boolean = false

    private var sec = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable1 = object : Runnable{
        override fun run() {
            sec = (sec + 1) % 3600

            bind!!.tvMinSec.text = String.format("%02d:%02d", (sec / 60) % 60, sec % 60)

            handler.postDelayed(this, 1000)
        }
    }

    private var millis = 0
    private val runnable2 = object : Runnable{
        override fun run() {
            millis += 16

            bind!!.tvMillis.text = String.format("%03d", millis % 1000)

            handler.postDelayed(this, 5)
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
        bind = FragmentStopwatchBinding.inflate(inflater, container, false)
        return bind!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners(){
        bind!!.btnPlayPause.setOnClickListener {
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

                handler.postDelayed(runnable1, 1000)
                handler.postDelayed(runnable2, 10)
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

                handler.removeCallbacks(runnable1)
                handler.removeCallbacks(runnable2)
            }
        }

        bind!!.btnReset.setOnClickListener {
            started = false
            millis = 0
            sec = 0

            bind!!.imageAvd.setImageResource(R.drawable.play_to_pause_avd)

            bind!!.circularBars.visibility = View.GONE
            bind!!.arcs.visibility = View.GONE

            bind!!.circularBars.stopAnimation()
            bind!!.arcs.stopAnimation()

            handler.removeCallbacks(runnable1)
            handler.removeCallbacks(runnable2)

            bind!!.tvMinSec.text = "00:00"
            bind!!.tvMillis.text = "000"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable1)
        handler.removeCallbacks(runnable2)
        bind = null
    }

}