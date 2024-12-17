package com.nextbigthing.pillpop

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextbigthing.pillpop.adapter.AlarmAdapter
import com.nextbigthing.pillpop.databinding.ActivityMainBinding
import com.nextbigthing.pillpop.databinding.AlarmScreenLayoutBinding
import com.nextbigthing.pillpop.model.Alarm

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val scheduledAlarms = mutableListOf<Alarm>()
    private lateinit var alarmAdapter: AlarmAdapter
    private val OVERLAY_PERMISSION_REQUEST_CODE = 101

    // Register the result for notification permission request
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please allow 'Display over other apps' permission", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }

        createNotificationChannel()
        checkNotificationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAlarmPermission()
        }

        alarmAdapter = AlarmAdapter(scheduledAlarms)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = alarmAdapter

        binding.floatingActionButton.setOnClickListener {
            val bindingAlarm = AlarmScreenLayoutBinding.inflate(layoutInflater)
            val alertDialog = android.app.AlertDialog.Builder(this).setView(bindingAlarm.root).create()

            bindingAlarm.button.setOnClickListener {
                val hour = bindingAlarm.timePicker2.hour
                val minute = bindingAlarm.timePicker2.minute
                val medicineName = bindingAlarm.medicineEditText.text.toString()

                if (medicineName.isNotBlank()) {
                    setUpAlarm(hour, minute, medicineName)
                    scheduledAlarms.add(Alarm(medicineName, hour, minute))
                    alarmAdapter.notifyItemInserted(scheduledAlarms.size - 1)
                } else {
                    Toast.makeText(this, "Please enter a valid medicine name", Toast.LENGTH_SHORT).show()
                }

                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    // Other methods (checkNotificationPermission, checkAlarmPermission, etc.) remain unchanged

    private fun setUpAlarm(hour: Int, minute: Int, medicineName: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                // If the time has already passed, schedule it for the next day
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("medicineName", medicineName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle to ensure precise timing even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Alarm set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel"
            val channelName = "Alarm Notifications"
            val description = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = description
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            // Request notification permission
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @SuppressLint("NewApi")
    private fun checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show()
                // Continue with overlay-related tasks
            } else {
                Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}