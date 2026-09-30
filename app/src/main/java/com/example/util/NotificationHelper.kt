package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    private const val CHANNEL_ID = "sawa_discreet_notifications"
    private const val CHANNEL_NAME = "تنبيهات سوا اللطيفة"
    private const val NOTIFICATION_ID_BASE = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "إشعارات هادئة ولطيفة تحافظ على الخصوصية والستر"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a discreet notification that protects user privacy on the phone surface:
     * Does not expose embarrassing or private romantic/marital details.
     */
    fun sendDiscreetNotification(
        context: Context,
        title: String = "تنبيه جديد من سوا 🌿",
        discreetMessage: String = "لديك نشاط وتحديث جديد في التطبيق",
        notificationId: Int = NOTIFICATION_ID_BASE
    ) {
        try {
            createNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(discreetMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE) // Hide sensitive content on lock screen

            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted or running in sandbox environment
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun notifyNewDiscreetMessage(context: Context, senderName: String = "عضو") {
        sendDiscreetNotification(
            context = context,
            title = "رسالة جديدة 💬",
            discreetMessage = "وصلتك رسالة جديدة في تطبيق سوا",
            notificationId = 1002
        )
    }

    fun notifyNewDiscreetLike(context: Context) {
        sendDiscreetNotification(
            context = context,
            title = "نشاط جديد في حسابك 🌟",
            discreetMessage = "هناك تفاعل جديد واهتمام بملفك الشخصي",
            notificationId = 1003
        )
    }

    fun notifyNewDiscreetRose(context: Context) {
        sendDiscreetNotification(
            context = context,
            title = "هدية لطيفة 🌹",
            discreetMessage = "تم إهداؤك باقة ورد جديدة في حسابك",
            notificationId = 1004
        )
    }
}
