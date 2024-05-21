package com.example.clocky.fragments

import AlarmViewModelFactory
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.clocky.R
import com.example.clocky.activities.CreateAlarmActivity
import com.example.clocky.adapter.AlarmAdapter
import com.example.clocky.broadcastReceiver.AlarmBroadCastReceiver
import com.example.clocky.data.AlarmDatabase
import com.example.clocky.databinding.FragmentAlarmBinding
import com.example.clocky.model.setAlarm
import com.example.clocky.repository.AlarmRepository
import com.example.clocky.viewmodel.AlarmViewModel
import java.util.Calendar

class AlarmFragment : Fragment() {
    private var bind : FragmentAlarmBinding? = null
    lateinit var alarmViewModel : AlarmViewModel
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentAlarmBinding.inflate(inflater, container, false)
        return bind!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
    }

    private fun setupViewModel() {
        val alarmRepository = AlarmRepository(AlarmDatabase(requireContext()))
        val viewModelProviderFactory = AlarmViewModelFactory(requireActivity().application, alarmRepository)
        alarmViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[AlarmViewModel::class.java] // [] here, same as get()

        alarmViewModel.getInsertedAlarm().observe(viewLifecycleOwner, Observer{alarm ->
            if(alarm != null)
            {
                if(!alarm.operated) {
                    alarm.setAlarm(requireContext())
                    alarm.operated = true
                    alarmViewModel.updateAlarm(alarm)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmAdapter(requireContext(), this)
        bind!!.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = alarmAdapter

            //getAllNotes will typically gets called once during the initialization, then after that LiveData
            //handles any change in the list as room updates the livedata object associated with a query with updated data
            alarmViewModel.getAllAlarms().observe(viewLifecycleOwner) { noteList ->
                alarmAdapter.differ.submitList(noteList)
            }
        }
    }

    private fun setupListeners(){
        bind!!.btnAddAlarm.setOnClickListener{
            startActivity(Intent(context, CreateAlarmActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bind = null
    }
}