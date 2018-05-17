package com.nightlydev.cryptocointracker.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.nightlydev.cryptocointracker.MainActivity
import com.nightlydev.cryptocointracker.R


/**
 * Created by edu on 1-2-18.
 */
class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "New message from: " + remoteMessage.from)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        if (remoteMessage.notification != null && remoteMessage.notification!!.body != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
            sendNotification(remoteMessage.notification!!.body!!)
        }
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        const val TAG = "MessagingService"
    }
}