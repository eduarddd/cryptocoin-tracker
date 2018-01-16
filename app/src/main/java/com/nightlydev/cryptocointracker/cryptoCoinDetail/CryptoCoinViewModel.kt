package com.nightlydev.cryptocointracker.cryptoCoinDetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 11-1-18
 */
class CryptoCoinViewModel(cryptoCoinId: String, displayHistoryPeriod: Int) : ViewModel() {
    private var mCryptoCoinId = MutableLiveData<String>()
    private var mCryptoCoin : LiveData<CryptoCoin>
    private var mDisplayHistoryPeriod : MutableLiveData<Int>
    private var mCryptoCoinHistory : LiveData<List<CryptoCoinHistoryPriceItem>?>? = null
    private val repository = CryptoCoinRepository()
    private var mIsFavorite : LiveData<Boolean>

    init {
        mCryptoCoinId.value = cryptoCoinId
        mCryptoCoin = Transformations.switchMap(mCryptoCoinId) {
            id -> repository.getCryptoCoin(id)
        }

        mDisplayHistoryPeriod = MutableLiveData()
        mDisplayHistoryPeriod.value = displayHistoryPeriod

        mIsFavorite = Transformations.map(repository.getFavorites()!!) {
            favorites -> (favorites.contains(mCryptoCoin.value))
        }
    }

    fun isFavorite() = mIsFavorite
    fun getCryptoCoin() = mCryptoCoin
    fun getDisplayHistoryPeriod() = mDisplayHistoryPeriod

    fun getCryptoCoinHistory() : LiveData<List<CryptoCoinHistoryPriceItem>?> {
        if (mCryptoCoinHistory == null) {
            mCryptoCoinHistory = Transformations.switchMap(mDisplayHistoryPeriod) {
                period -> repository.getCryptoCoinPriceHistory(period, mCryptoCoin.value!!.short)
            }
        }
        return mCryptoCoinHistory!!
    }

    fun saveFavorite() {
        if (!mIsFavorite.value!!) {
            repository.saveFavorite(mCryptoCoin.value)
        } else {
            repository.removeFavorite(mCryptoCoin.value!!)
        }
    }
}