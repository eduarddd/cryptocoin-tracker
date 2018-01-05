package com.nightlydev.cryptocointracker

import android.app.Application
import android.arch.persistence.room.Room
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
        cryptoCoinDatabase = Room
                .databaseBuilder(
                        this,
                        CryptoCoinDatabase::class.java,
                        "crypto-coin"
                )
                .fallbackToDestructiveMigration()
                .build()
    }
}