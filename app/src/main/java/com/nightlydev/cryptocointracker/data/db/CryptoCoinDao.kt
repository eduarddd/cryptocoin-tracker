package com.nightlydev.cryptocointracker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Flowable

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 20-12-17
 */
@Dao interface CryptoCoinDao {
    @Query("SELECT * FROM cryptoCoin ORDER BY rank")
    fun getAllCryptoCoins(): Flowable<List<CryptoCoin>>

    @Insert
    fun insert(cryptoCoin: CryptoCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cryptoCoinList: List<CryptoCoin>)
}