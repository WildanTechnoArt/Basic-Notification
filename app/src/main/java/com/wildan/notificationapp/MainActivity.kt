package com.wildan.notificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private val mReceiver = NotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
        btn_notify.setOnClickListener {
            sendNotification()
        }
        btn_update.setOnClickListener {
            updateNotification()
        }
        btn_cancel.setOnClickListener {
            cancelNotification()
        }
        createNotificationChannel()
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Mengecek versi dari perangkan android yang digunakan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*
            Jika menggunakan versi Android O atau lebih baru
            Maka buatlah Notification Channel
             */

            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID, "Hanya Percobaan", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.RED
                enableLights(true)
                enableVibration(true)
                description = "Contoh Notifikasi"
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Method untuk mengatur perilaku konten didalam Notifikasi
    private fun getNotificationBuilder(): NotificationCompat.Builder {
        // Memberikan aksi saat notifikasi diklik
        val notifyIntent = Intent(this, NotificationActivity::class.java)
        val notifyPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle("Judul Notifikasi")
            setContentText("Konten dari Notifikasi")
            setContentIntent(notifyPendingIntent)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setDefaults(NotificationCompat.DEFAULT_ALL)
        }
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updateIntentFilter = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT
        )

        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updateIntentFilter)
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    fun updateNotification() {
        // Merubah file gambar kedalam Bitmap
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.kucing)

        // Memunculkan Gambar pada notifikasi yang muncul
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(
            NotificationCompat
                .BigPictureStyle()
                .bigPicture(bitmap)
                .setBigContentTitle("Content Update!")
        )

        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
    }

    private fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean
    ) {
        btn_notify.isEnabled = isNotifyEnabled
        btn_update.isEnabled = isUpdateEnabled
        btn_cancel.isEnabled = isCancelEnabled
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val ACTION_UPDATE_NOTIFICATION =
            "com.wildan.notificationapp.ACTION_UPDATE_NOTIFICATION"
        const val NOTIFICATION_ID = 0
    }

    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }
}