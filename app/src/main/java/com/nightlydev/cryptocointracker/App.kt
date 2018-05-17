package com.nightlydev.cryptocointracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.persistence.room.Room
import android.os.Build
import com.nightlydev.cryptocointracker.data.db.CryptoCoinDatabase

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
class App : Application() {

    companion object {
        var cryptoCoinDatabase: CryptoCoinDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        initDatabase()
        createNotificationChannel()
    }

    private fun initDatabase() {
        cryptoCoinDatabase = Room
                .databaseBuilder(
                        this,
                        CryptoCoinDatabase::class.java,
                        "crypto-coin"
                )
                .fallbackToDestructiveMigration()
                .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.default_notification_channel_id)
            val name = getString(R.string.default_notification_channel_name)
            val description = getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}