package com.nightlydev.cryptocointracker.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
@Database(entities = [(CryptoCoin::class), FavoriteCryptoCoin::class], version = 1)
abstract class CryptoCoinDatabase : RoomDatabase() {
    abstract fun cryptoCoinDao(): CryptoCoinDao
    abstract fun favoriteCryptoCoinDao(): FavoriteCryptoCoinDao
}