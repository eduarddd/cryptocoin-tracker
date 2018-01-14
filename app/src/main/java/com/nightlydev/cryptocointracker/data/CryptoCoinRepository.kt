package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.nightlydev.cryptocointracker.App
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import com.nightlydev.cryptocointracker.data.response.priceHistory
import com.nightlydev.cryptocointracker.model.CryptoCoin
import com.nightlydev.cryptocointracker.model.FavoriteCryptoCoin
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
    private val db = App.cryptoCoinDatabase

    fun listCryptoCoins(): Observable<List<CryptoCoin>> = cryptoCoinService.listCryptoCoins()
    fun getCryptoCoin(cryptoCoinId: Long) =
            db?.cryptoCoinDao()?.getCryptoCoin(cryptoCoinId = cryptoCoinId)!!
    fun getFavorites() : LiveData<List<CryptoCoin>>? =
            db?.favoriteCryptoCoinDao()?.getFavorites()

    fun getCryptoCoinPriceHistory(dayCount: Int, coinSymbol: String)
            : LiveData<List<CryptoCoinHistoryPriceItem>?> {
        val priceHistory = MutableLiveData<List<CryptoCoinHistoryPriceItem>?>()
        val observable : Observable<CryptoCoinHistoryResponse>
        if (dayCount > -1) {
            observable = cryptoCoinService.listCryptoCoinHistory(dayCount, coinSymbol)
        }else {
            observable = cryptoCoinService.listCryptoCoinHistory(coinSymbol)
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> priceHistory.value = result.priceHistory()
                },{
                    error ->
                    run {
                        error.printStackTrace()
                        priceHistory.value = null
                    }
                })
        return priceHistory
    }

    fun saveFavorite(cryptoCoin: CryptoCoin?) {
        val favorite = FavoriteCryptoCoin()
        favorite.cryptoCoinId = cryptoCoin!!.id
        Single.fromCallable {
            db?.favoriteCryptoCoinDao()?.insert(favorite)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeFavorite(cryptoCoin: CryptoCoin) {
        Single.fromCallable {
            db?.favoriteCryptoCoinDao()?.delete(cryptoCoin.id)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}