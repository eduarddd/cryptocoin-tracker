package com.nightlydev.cryptocointracker.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
@Dao interface CryptoCoinDao {
    @Query("SELECT * FROM cryptoCoin ORDER BY market_cap DESC LIMIT 100 OFFSET :offset")
    fun getAllCryptoCoins(offset: Int = 0): LiveData<List<CryptoCoin>>

    @Query("SELECT * FROM cryptoCoin WHERE id = :cryptoCoinId LIMIT 1")
    fun getCryptoCoin(cryptoCoinId: String) : LiveData<CryptoCoin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCryptoCoins(cryptoCoinList: List<CryptoCoin>)
}