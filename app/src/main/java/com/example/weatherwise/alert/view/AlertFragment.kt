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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.alert.ALERT_DESC
import com.example.weatherwise.alert.CHANNEL_ID
import com.example.weatherwise.alert.NOTIFICATION_ID
import com.example.weatherwise.alert.NotificationReceiver
import com.example.weatherwise.alert.viewmodel.AlertViewModel
import com.example.weatherwise.alert.viewmodel.AlertViewModelFactory
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.view.HomeHourlyAdapter
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlertFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val TAG = "AlertFragment"


    private lateinit var fabAddAlert: FloatingActionButton


    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0

    private var savedDay: Int = 0
    private var savedMonth: Int = 0
    private var savedYear: Int = 0
    private var savedHour: Int = 0
    private var savedMinute: Int = 0

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory

    private lateinit var pendingIntent: PendingIntent

    private lateinit var broadcastIntent: Intent

    val calender = Calendar.getInstance()

    private lateinit var rvAlerts : RecyclerView
    private lateinit var alertAdapter : AlertAdapter


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
        rvAlerts = view.findViewById(R.id.rv_alerts)

        alertAdapter = AlertAdapter(requireContext())
        rvAlerts.adapter = alertAdapter
        rvAlerts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )


        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

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


    private fun pickDate() {
        fabAddAlert.setOnClickListener {
            year = calender.get(Calendar.YEAR)
            month = calender.get(Calendar.MONTH)
            day = calender.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    private fun scheduleNotification(dateTimeInMillis: Long) {

        alertViewModel.setAlertLocation("33.44", "-94.04", "en", "metric")
        lifecycleScope.launch {
            alertViewModel.alertWeather.collect { result ->

                when (result) {
                    is ApiState.Loading -> {
                        //progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        //progressBar.visibility = View.GONE

                        Log.d(TAG, "Success Result: ${result.data.alerts} ")
                        val goodWeather = "Good Weather, Enjoy"

                        val desc =
                            if (result.data.alerts.isNullOrEmpty() || result.data.alerts[0].description == null) {

                                goodWeather
                            } else {

                                result.data.alerts[0].description
                            }

                        val currentAlert = result.data.alerts?.get(0)

                        if ( currentAlert != null){
                            alertViewModel.insertAlert(currentAlert)

                        }
                        else{
                            val emptyAlert = Alert(senderName = "No Alerts", event = "No Events", start = 0, end = 0, description = "Good Weather Enjoy")
                            alertViewModel.insertAlert(emptyAlert)
                        }




                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "current Alert: ${alertViewModel.getAllAlerts()} ")
                            lifecycleScope.launch {
                                if(result.data.alerts.isNullOrEmpty()){
                                    alertViewModel.currentAlert.collectLatest{
                                        alertAdapter.submitList(it)
                                    }


                                }
                                else{
                                    alertAdapter.submitList(result.data.alerts)
                                }
                            }


                            broadcastIntent = Intent(
                                requireActivity().applicationContext,
                                NotificationReceiver::class.java
                            )
                            broadcastIntent.putExtra(ALERT_DESC, desc)

                            Log.d(TAG, "scheduleNotification: $desc")
                            pendingIntent = PendingIntent.getBroadcast(
                                requireContext(),
                                NOTIFICATION_ID, broadcastIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )

                            val alarmManager =
                                requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    dateTimeInMillis,
                                    pendingIntent
                                )
                            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    dateTimeInMillis,
                                    pendingIntent
                                )
                            } else {
                                alarmManager.set(
                                    AlarmManager.RTC_WAKEUP,
                                    dateTimeInMillis,
                                    pendingIntent
                                )
                            }

                        }


                    }

                    is ApiState.Failure -> {
                        //progressBar.visibility = View.GONE
                        Log.d(TAG, "Exception is: ${result.msg}")
                        Toast.makeText(requireActivity(), result.msg.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
            }


        }


    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Channel"
            val desc = "Weather Desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }
            val notificationManger =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(channel)

        }

    }


}