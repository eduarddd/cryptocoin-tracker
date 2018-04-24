package com.nightlydev.cryptocointracker.cryptoCoinOverview

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 14-1-18
 */
class OverviewViewModel : ViewModel() {
    private var mCryptoCoinList : LiveData<PagedList<CryptoCoin>>
    private val mSearchQuery = MutableLiveData<String>()
    private val repository = CryptoCoinRepository()

    init {
        mCryptoCoinList = repository.getAllCryptoCoins()
        refreshCryptoCoinList()
    }

    fun getCryptoCoinList() = mCryptoCoinList
    fun refreshCryptoCoinList() = repository.refreshCryptoCoinList()
    fun getSearchQuery() = mSearchQuery
}