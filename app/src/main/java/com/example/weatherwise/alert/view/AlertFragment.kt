package com.example.weatherwise.alert.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.example.weatherwise.R
import com.example.weatherwise.alert.CHANNEL_ID
import com.example.weatherwise.alert.NOTIFICATION_ID
import com.example.weatherwise.alert.NotificationReceiver
import com.example.weatherwise.alert.TIME_IN_MILLIS
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AlertFragment : Fragment(), DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private lateinit var fabAddAlert : FloatingActionButton
    private val TAG = "AlertFragment"
    private  var day : Int = 0
    private  var month : Int = 0
    private  var year : Int = 0
    private  var hour : Int = 0
    private  var minute : Int = 0

    private  var savedDay : Int = 0
    private  var savedMonth : Int = 0
    private  var savedYear : Int = 0
    private  var savedHour : Int = 0
    private  var savedMinute : Int = 0

    val calender = Calendar.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddAlert = view.findViewById(R.id.fab_add_alert)

        pickDate()

    }





    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        hour = calender.get(Calendar.HOUR_OF_DAY)
        minute = calender.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), this, hour, minute, false).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        Log.d(TAG, "Date and Time: $savedDay $savedMonth $savedYear $savedHour $savedMinute ")


        val timeInMillis = getDateTimeCalender()
        Log.d(TAG, "timeInMillis: $timeInMillis ")


        scheduleNotification(timeInMillis)
    }



    private fun getDateTimeCalender(): Long {
        val calendar = Calendar.getInstance()

        calendar.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d(TAG, "Chosen Date and Time: ${sdf.format(calendar.time)}")

        return calendar.timeInMillis
    }


    private fun pickDate(){
        fabAddAlert.setOnClickListener {
            year = calender.get(Calendar.YEAR)
            month = calender.get(Calendar.MONTH)
            day = calender.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(),this,year,month,day).show()
        }
    }

    private fun scheduleNotification(dateTimeInMillis: Long) {

        val intent = Intent(requireActivity().applicationContext, NotificationReceiver::class.java)
        intent.putExtra(TIME_IN_MILLIS, dateTimeInMillis)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(),
            NOTIFICATION_ID, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent)
        } else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeInMillis, pendingIntent)
        }
    }



    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Weather Channel"
            val desc = "Weather Desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = desc
            }
            val notificationManger= requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(channel)

        }

    }


}