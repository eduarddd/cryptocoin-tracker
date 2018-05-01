package com.nightlydev.cryptocointracker.cryptoCoinOverview

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.data.Resource
import com.nightlydev.cryptocointracker.model.CryptoCoin
import java.util.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 14-1-18
 */
class OverviewViewModel : ViewModel() {
    val cryptoCoinList : LiveData<Resource<List<CryptoCoin>?>>
    private val mSearchQuery = MutableLiveData<String>()
    private val repository = CryptoCoinRepository()
    private val refreshTime : MutableLiveData<Calendar> = MutableLiveData()

    init {
        cryptoCoinList = Transformations.switchMap(refreshTime) { repository.getCryptoCoins() }
        refreshCryptoCoinList()
    }

    fun refreshCryptoCoinList() {
        refreshTime.value = Calendar.getInstance()
    }
    fun getSearchQuery() = mSearchQuery
}