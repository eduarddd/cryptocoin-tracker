package com.nightlydev.cryptocointracker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin
import io.reactivex.Flowable

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 9-1-18
 */
@Dao interface FavoriteCryptoCoinDao {
    @Query("SELECT * FROM favoriteCryptoCoin")
    fun getFavorites(): Flowable<List<FavoriteCryptoCoin>>

    @Query("SELECT * FROM favoriteCryptoCoin WHERE cryptoCoinId = :arg0 LIMIT 1")
    fun findFavorite(cryptoCoinId: Long): Flowable<FavoriteCryptoCoin?>

    @Insert
    fun insert(cryptoCoin: CryptoCoin)

    @Delete
    fun delete(cryptoCoin: CryptoCoin)
}