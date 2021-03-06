package com.nightlydev.cryptocointracker.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 9-1-18
 */
@Dao interface FavoriteCryptoCoinDao {
    @Query("SELECT * FROM favoriteCryptoCoin " +
            "INNER JOIN cryptoCoin ON favoriteCryptoCoin.crypto_coin_id = cryptoCoin.id " +
            "ORDER BY cryptoCoin.market_cap DESC ")
    fun getFavorites(): LiveData<List<CryptoCoin>>

    @Query("SELECT * FROM favoriteCryptoCoin " +
            "INNER JOIN cryptoCoin ON favoriteCryptoCoin.crypto_coin_id = cryptoCoin.id " +
            "WHERE crypto_coin_id = :cryptoCoinId LIMIT 1")
    fun findFavorite(cryptoCoinId: String): LiveData<CryptoCoin?>

    @Insert(onConflict = REPLACE)
    fun insert(cryptoCoin: FavoriteCryptoCoin)

    @Query("DELETE FROM favoriteCryptoCoin WHERE crypto_coin_id = :cryptoCoinId")
    fun delete(cryptoCoinId: String)
}