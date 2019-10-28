package com.wildan.notificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_notify.setOnClickListener {
            sendNotification()
        }
        createNotificationChannel()
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
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle("Judul Notifikasi")
            setContentText("Konten dari Notifikasi")
        }
    }

    private fun sendNotification() {
        val notifyBuilder = getNotificationBuilder()
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_ID = 0
    }
}