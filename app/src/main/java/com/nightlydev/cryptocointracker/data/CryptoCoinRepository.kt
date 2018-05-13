package com.nightlydev.cryptocointracker.data

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.nightlydev.cryptocointracker.App
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
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
@SuppressLint("CheckResult")
class CryptoCoinRepository {
    private val cryptoCoinService: CryptoCoinService = CryptoCoinService.create()
    private val cryptoCoinDao = App.cryptoCoinDatabase?.cryptoCoinDao()
    private val favoriteCryptoCoinDao = App.cryptoCoinDatabase?.favoriteCryptoCoinDao()

    fun getAllCryptoCoins() : LiveData<List<CryptoCoin>>? {
        return cryptoCoinDao?.getAllCryptoCoins()
    }

    fun getCryptoCoins() : LiveData<Resource<List<CryptoCoin>?>> {
        return object : NetworkBoundResource<List<CryptoCoin>, List<CryptoCoin>>() {
            override fun saveCallResult(item: List<CryptoCoin>) {
                cryptoCoinDao!!.updateCryptoCoins(item)
            }

            override fun shouldFetch(data: List<CryptoCoin>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<CryptoCoin>> {
                return cryptoCoinDao!!.getAllCryptoCoins()
            }

            override fun createObservable(): Observable<List<CryptoCoin>>? {
                return cryptoCoinService.listCryptoCoins()
            }

        }.asLiveData()
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

    fun getCryptoCoin(cryptoCoinId: String) = cryptoCoinDao?.getCryptoCoin(cryptoCoinId = cryptoCoinId)

    fun getFavorites() : LiveData<List<CryptoCoin>>? = favoriteCryptoCoinDao?.getFavorites()

    fun getCryptoCoinPriceHistory(dayCount: Int, coinSymbol: String)
            : LiveData<Resource<List<CryptoCoinHistoryPriceItem>?>> {
        val priceHistory = MutableLiveData<Resource<List<CryptoCoinHistoryPriceItem>?>>()
        priceHistory.value = Resource.loading(null)
        val observable = if (dayCount > -1) {
            cryptoCoinService.listCryptoCoinHistory(dayCount, coinSymbol)
        }else {
            cryptoCoinService.listCryptoCoinHistory(coinSymbol)
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> priceHistory.value = Resource.success(result.priceHistory())
                },{
                    error -> run {
                        error.printStackTrace()
                        priceHistory.value = Resource.error(message = "Error loading history")
                }})
        return priceHistory
    }

    fun saveFavorite(cryptoCoin: CryptoCoin?) {
        val favorite = FavoriteCryptoCoin()
        favorite.cryptoCoinId = cryptoCoin!!.symbol
        Single.fromCallable {
            favoriteCryptoCoinDao?.insert(favorite)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeFavorite(cryptoCoin: CryptoCoin) {
        Single.fromCallable {
            favoriteCryptoCoinDao?.delete(cryptoCoin.symbol)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}