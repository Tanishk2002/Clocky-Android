package com.example.clocky.fragments

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clocky.R
import com.example.clocky.databinding.FragmentClockBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ClockFragment : Fragment() {

    private var bind : FragmentClockBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentClockBinding.inflate(inflater, container, false)
        return bind!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners(){
        bind!!.date.text = getCurrentDate()
    }

    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH)
        return dateFormat.format(currentDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bind = null
    }
}