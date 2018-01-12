package com.nightlydev.cryptocointracker.data

import com.nightlydev.cryptocointracker.App
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinRepository {

    private val cryptoCoinService: CryptoCoinService = CryptoCoinService.create()

    fun listCryptoCoins(): Observable<List<CryptoCoin>> {
        return cryptoCoinService.listCryptoCoins()
    }

    fun listFavorites() : Flowable<List<CryptoCoin>>? {
        return App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.getFavorites()
    }

    fun listCryptoCoinHistory(dayCount: Int,
                              coinSymbol: String): Observable<CryptoCoinHistoryResponse> {
        return if (dayCount > -1) {
            cryptoCoinService.listCryptoCoinHistory(dayCount, coinSymbol)
        } else {
            cryptoCoinService.listCryptoCoinHistory(coinSymbol)
        }
    }

    fun findFavorite(cryptoCoin: CryptoCoin) : Single<FavoriteCryptoCoin?>? {
        return App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.findFavorite(cryptoCoin.id)
    }

    fun saveFavorite(cryptoCoin: CryptoCoin) {
        val favorite = FavoriteCryptoCoin()
        favorite.cryptoCoinId = cryptoCoin.id
        Single.fromCallable {
            App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.insert(favorite)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeFavorite(cryptoCoin: CryptoCoin) {
        Single.fromCallable {
            App.cryptoCoinDatabase?.favoriteCryptoCoinDao()?.delete(cryptoCoin.id)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}