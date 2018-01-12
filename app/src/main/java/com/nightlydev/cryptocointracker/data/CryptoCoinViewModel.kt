package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.data.response.priceHistory
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 11-1-18
 */
class CryptoCoinViewModel(cryptoCoin: CryptoCoin) : ViewModel() {
    private val mCryptoCoin = MutableLiveData<CryptoCoin>()
    private val mCryptoCoinHistory = MutableLiveData<List<CryptoCoinHistoryPriceItem>>()
    private val repository = CryptoCoinRepository()
    private val mIsFavorite = MutableLiveData<Boolean>()

    init {
        mCryptoCoin.value = cryptoCoin
        mIsFavorite.value = false

        Single.fromCallable {
            repository.findFavorite(cryptoCoin)
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    _ -> mIsFavorite.value = true
                },{
                    error -> mIsFavorite.value = false
                })

    }

    fun isFavorite() : MutableLiveData<Boolean> {
        return mIsFavorite
    }

    fun getCryptoCoin() : MutableLiveData<CryptoCoin> {
        return mCryptoCoin
    }

    fun getCryptoCoinHistory() : MutableLiveData<List<CryptoCoinHistoryPriceItem>> {
        return mCryptoCoinHistory
    }

    fun fetchCryptoCoinHistory(period: Int) {
        repository.listCryptoCoinHistory(period, mCryptoCoin.value!!.short)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> mCryptoCoinHistory.value = result.priceHistory()
                }, {
                    error -> error.printStackTrace()
                })
    }

    fun saveFavorite() {
        if (!mIsFavorite.value!!) {
            repository.saveFavorite(mCryptoCoin.value!!)
            mIsFavorite.value = true
        } else {
            mIsFavorite.value = false
            repository.removeFavorite(mCryptoCoin.value!!)
        }
    }
}