package com.nightlydev.cryptocointracker.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
@Dao interface CryptoCoinDao {
    @Query("SELECT * FROM cryptoCoin ORDER BY market_cap DESC LIMIT 100")
    fun getAllCryptoCoins(): LiveData<List<CryptoCoin>>

    @Query("SELECT * FROM cryptoCoin WHERE id = :arg0 LIMIT 1")
    fun getCryptoCoin(cryptoCoinId: String) : LiveData<CryptoCoin>

    @Update()
    fun updateCryptoCoins(cryptoCoinList: List<CryptoCoin>)
}