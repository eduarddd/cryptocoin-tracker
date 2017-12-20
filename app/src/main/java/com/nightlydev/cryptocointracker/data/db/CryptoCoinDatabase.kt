package com.nightlydev.cryptocointracker.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
@Database(entities = [(CryptoCoin::class)], version = 1)
abstract class CryptoCoinDatabase : RoomDatabase() {
    abstract fun cryptoCoinDao(): CryptoCoinDao
}