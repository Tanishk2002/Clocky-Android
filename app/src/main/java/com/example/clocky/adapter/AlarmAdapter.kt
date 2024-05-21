package com.example.clocky.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clocky.R
import com.example.clocky.databinding.AlarmItemBinding
import com.example.clocky.fragments.AlarmFragment
import com.example.clocky.model.Alarm
import com.example.clocky.model.cancelAlarm
import com.example.clocky.model.setAlarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmAdapter(private val context: Context, private val alarmFragment: AlarmFragment) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(var itemBind: AlarmItemBinding) : RecyclerView.ViewHolder(itemBind.root)

    //diff callback mechanism to efficiently update contents of the list without re-binding all the items
    //when dataset changes
    private val diffCallback = object : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return true
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val currentAlarmOb = differ.currentList[position]

        setAllViews(holder, currentAlarmOb)
        setAlarmItemListeners(holder, currentAlarmOb)
    }

    private fun setAllViews(holder: AlarmViewHolder, alarm: Alarm) {
        holder.itemBind.alarmTitle.text = alarm.title
        holder.itemBind.alarmTime.text =
            "${String.format("%02d", alarm.hour)}:${String.format("%02d", alarm.minute)}"
        holder.itemBind.alarmSwitch.isChecked = alarm.enabled

        if (recurring(alarm)) {
            holder.itemBind.alarmDate.visibility = View.GONE

            holder.itemBind.weekDays.text = getWeekDaysString(alarm)
            holder.itemBind.weekDays.visibility = View.VISIBLE
        } else {
            holder.itemBind.weekDays.visibility = View.GONE

            holder.itemBind.alarmDate.text = getAlarmDateString(alarm, alarm.timeInMillis)
            holder.itemBind.alarmDate.visibility = View.VISIBLE
        }
    }

    private fun setAlarmItemListeners(holder: AlarmViewHolder, alarm: Alarm) {
        holder.itemBind.alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            alarm.enabled = isChecked
            if (isChecked) {
                alarm.timeInMillis = getNextAlarmTime(alarm, alarm.timeInMillis)
                alarm.setAlarm(context)
                alarm.operated = true
                alarmFragment.alarmViewModel.updateAlarm(alarm)

            } else {
                alarm.cancelAlarm(context)
                alarm.operated = true
                alarmFragment.alarmViewModel.updateAlarm(alarm)
            }
        }

        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)

            val view =
                LayoutInflater.from(context).inflate(R.layout.delete_dialog_layout, null, false)
            val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
            val btnOk = view.findViewById<TextView>(R.id.btnOk)

            builder.setView(view)

            var dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnOk.setOnClickListener {
                if (alarm.enabled)
                    alarm.cancelAlarm(context)
                alarmFragment.alarmViewModel.deleteAlarm(alarm.id)
                dialog.dismiss()
            }

            dialog.show()

            true
        }
    }

    private fun recurring(alarm: Alarm): Boolean {
        return alarm.mon || alarm.tu || alarm.wed || alarm.th || alarm.fri || alarm.sat || alarm.sun
    }

    private fun getAlarmDateString(alarm: Alarm, alarmTimeInMillis: Long): String {
        val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

        if (alarmTimeInMillis > System.currentTimeMillis())
            return sdf.format(alarmTimeInMillis)
        else {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, alarm.hour)
            today.set(Calendar.MINUTE, alarm.minute)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            //if time is past, setting time for the next day
            if (today.timeInMillis <= System.currentTimeMillis())
                today.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1)
            return sdf.format(today.timeInMillis)
        }
    }

    private fun getWeekDaysString(alarm: Alarm): SpannableString {
        val colorNormal = ContextCompat.getColor(context, R.color.theme_color_secondary)
        val colorHighlighted = ContextCompat.getColor(context, R.color.theme_color)

        val weekDaysList =
            arrayOf(alarm.mon, alarm.tu, alarm.wed, alarm.th, alarm.fri, alarm.sat, alarm.sun)

        val weekDaysText = "M T W T F S S"
        val spannableString = SpannableString(weekDaysText)
        var index = 0

        for (item in weekDaysList) {
            if (item) {
                spannableString.setSpan(
                    ForegroundColorSpan(colorHighlighted),
                    index,
                    index + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(
                    ForegroundColorSpan(colorNormal),
                    index,
                    index + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            index += 2
        }
        return spannableString
    }

    private fun getNextAlarmTime(alarm: Alarm, alarmTimeInMillis: Long): Long {
        if (alarmTimeInMillis > System.currentTimeMillis())
            return alarmTimeInMillis

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, alarm.hour)
        today.set(Calendar.MINUTE, alarm.minute)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        //if time is past, setting time for the next day
        if (today.timeInMillis <= System.currentTimeMillis())
            today.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1)
        return today.timeInMillis
    }

}