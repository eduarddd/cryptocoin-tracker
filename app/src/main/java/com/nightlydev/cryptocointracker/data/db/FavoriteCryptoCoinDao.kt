package com.nightlydev.cryptocointracker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 9-1-18
 */
@Dao interface FavoriteCryptoCoinDao {
    @Query("SELECT * FROM favoriteCryptoCoin "
            + "INNER JOIN cryptoCoin ON favoriteCryptoCoin.crypto_coin_id = cryptoCoin.id ")
    fun getFavorites(): Flowable<List<CryptoCoin>>

    @Query("SELECT * FROM favoriteCryptoCoin WHERE crypto_coin_id = :arg0 LIMIT 1")
    fun findFavorite(cryptoCoinId: Long): Single<FavoriteCryptoCoin?>

    @Insert(onConflict = REPLACE)
    fun insert(cryptoCoin: FavoriteCryptoCoin)

    @Query("DELETE FROM favoriteCryptoCoin WHERE crypto_coin_id = :arg0")
    fun delete(cryptoCoinId: Long)
}