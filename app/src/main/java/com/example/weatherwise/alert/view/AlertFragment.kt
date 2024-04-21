package com.example.weatherwise.alert.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
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
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherwise.R
import com.example.weatherwise.alert.CHANNEL_ID
import com.example.weatherwise.alert.NotificationReceiver
import com.example.weatherwise.alert.viewmodel.AlertViewModel
import com.example.weatherwise.alert.viewmodel.AlertViewModelFactory
import com.example.weatherwise.databinding.FragmentAlertBinding
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.model.entities.Alert
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.util.ChecksManager
import com.example.weatherwise.util.LATITUDE
import com.example.weatherwise.util.LOCATION
import com.example.weatherwise.util.LONGITUDE
import com.example.weatherwise.util.getAddress
import com.example.weatherwise.util.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val ALERT_TYPE = "alarm_type"

class AlertFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, OnAlertClickListener {

    private val TAG = "AlertFragment"

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

    private lateinit var binding: FragmentAlertBinding

    private lateinit var sdf: SimpleDateFormat
    private lateinit var calendar: Calendar

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory

    private lateinit var pendingIntent: PendingIntent

    private lateinit var broadcastIntent: Intent
    private lateinit var locationSharedPreferences: SharedPreferences

    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private var selectedOption = ""
    val calender = Calendar.getInstance()

    private lateinit var alertAdapter: AlertAdapter
    private var insertedAlert: Alert? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var emptyAnimationAlert: LottieAnimationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcastIntent = Intent(
            requireActivity().applicationContext,
            NotificationReceiver::class.java
        )
        alarmManager =
            requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager


        createNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationSharedPreferences =
            requireContext().getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

        alertAdapter = AlertAdapter(requireContext(), this)
        binding.rvAlerts.adapter = alertAdapter
        binding.rvAlerts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        alertViewModelFactory = AlertViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )


        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        lifecycleScope.launch {
            alertViewModel.allAlerts.collect {

                if (it.isEmpty()){
                    binding.emptyAnimationViewAlert.visibility = View.VISIBLE
                }
                else{
                    binding.emptyAnimationViewAlert.visibility = View.GONE
                    alertAdapter.submitList(it)
                }
            }

        }


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


        val dateTimeInMillis = getDateTimeCalender()
        Log.d(TAG, "timeInMillis: $dateTimeInMillis ")



        lifecycleScope.launch {
            latitudeFromPrefs =
                locationSharedPreferences.getString(LATITUDE, null)
            longitudeFromPrefs =
                locationSharedPreferences.getString(LONGITUDE, null)

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address =
                geocoder.getAddress(
                    latitudeFromPrefs?.toDouble()?.round(4) ?: 0.0,
                    longitudeFromPrefs?.toDouble()?.round(4) ?: 0.0
                )
            val city = address?.locality ?: address?.extras?.getString("sub-admin", "Unknown area") ?: "Unknown"
            val dateAndTime = sdf.format(calendar.time).split(" ")

            withContext(Dispatchers.Main) {
                insertedAlert = Alert(location = city, date = dateAndTime[0], time = dateAndTime[1])
                alertViewModel.insertAlert(insertedAlert)
                Log.d(TAG, "timeInMillis inserted: $dateTimeInMillis")
                scheduleNotification(dateTimeInMillis)

            }


        }


    }


    private fun getDateTimeCalender(): Long {
        calendar = Calendar.getInstance()

        calendar.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)

        sdf = SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.getDefault())

        return calendar.timeInMillis
    }


    private fun pickDate() {
        binding.fabAddAlert.setOnClickListener {

            if(ChecksManager.checkConnection(requireContext())){
                showNotificationAlarmDialog { alertType ->
                    if (alertType == "Alarm") {
                        if (ChecksManager.isDrawOverlayPermissionGranted(requireActivity())) {
                            year = calender.get(Calendar.YEAR)
                            month = calender.get(Calendar.MONTH)
                            day = calender.get(Calendar.DAY_OF_MONTH)
                            DatePickerDialog(requireContext(), this, year, month, day).show()


                        } else {
                            ChecksManager.requestDrawOverlayPermission(requireActivity())
                        }
                    } else if (alertType == "Notification") {

                        if (ChecksManager.notificationPermission(requireActivity())) {
                            year = calender.get(Calendar.YEAR)
                            month = calender.get(Calendar.MONTH)
                            day = calender.get(Calendar.DAY_OF_MONTH)
                            DatePickerDialog(requireContext(), this, year, month, day).show()


                        } else {
                            ChecksManager.requestNotificationPermission(requireActivity())
                        }
                    }

                }
            }

            else{
                Toast.makeText(requireContext(),"Check Your Internet Connection",  Toast.LENGTH_SHORT).show()
            }




        }
    }

    private fun scheduleNotification(dateTimeInMillis: Long) {

        broadcastIntent.putExtra(ALERT_TYPE, selectedOption)
        Log.d(TAG, "scheduleNotification: $selectedOption")
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            dateTimeInMillis.toInt(), broadcastIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


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

    override fun deleteAlert(alert: Alert) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            lifecycleScope.launch {
                alertViewModel.deleteAlert(alert)
                val timeInMillis = getDateTimeCalender().toInt()
                pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    timeInMillis, broadcastIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                Log.d(TAG, "deleteAlert id: $timeInMillis ")
                alarmManager.cancel(pendingIntent)

            }
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }


    fun showNotificationAlarmDialog(callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Option")

        val options = arrayOf("Notification", "Alarm")
        var checkedItem = 0

        builder.setSingleChoiceItems(options, checkedItem) { _, which ->
            checkedItem = which
        }

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            selectedOption = options[checkedItem]
            callback(selectedOption)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}

