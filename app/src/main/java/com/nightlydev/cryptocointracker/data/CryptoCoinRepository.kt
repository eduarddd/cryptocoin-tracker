package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
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
    private val cryptoCoinDao = App.cryptoCoinDatabase?.cryptoCoinDao()
    private val favoriteCryptoCoinDao = App.cryptoCoinDatabase?.favoriteCryptoCoinDao()

    fun getAllCryptoCoins() : LiveData<PagedList<CryptoCoin>> {
        return LivePagedListBuilder<Int, CryptoCoin>(cryptoCoinDao?.getAllCryptoCoins()!!, 20
        ).build()
    }

    fun refreshCryptoCoinList() {
        cryptoCoinService.listCryptoCoins()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> updateCryptoCoins(result)
                },{
                    error -> run {
                    error.printStackTrace()
                }})
    }

    private fun updateCryptoCoins(cryptoCoinList: List<CryptoCoin>) {
        Single.fromCallable {
            cryptoCoinDao?.updateCryptoCoins(cryptoCoinList)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun getCryptoCoin(cryptoCoinId: String) =
            cryptoCoinDao?.getCryptoCoin(cryptoCoinId = cryptoCoinId)!!

    fun getFavorites() : LiveData<List<CryptoCoin>>? =
            favoriteCryptoCoinDao?.getFavorites()

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
                    error -> run {
                        error.printStackTrace()
                        priceHistory.value = null
                }})
        return priceHistory
    }

    fun saveFavorite(cryptoCoin: CryptoCoin?) {
        val favorite = FavoriteCryptoCoin()
        favorite.cryptoCoinId = cryptoCoin!!.shortName
        Single.fromCallable {
            favoriteCryptoCoinDao?.insert(favorite)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeFavorite(cryptoCoin: CryptoCoin) {
        Single.fromCallable {
            favoriteCryptoCoinDao?.delete(cryptoCoin.shortName)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}