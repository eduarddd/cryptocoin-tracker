package com.nightlydev.cryptocointracker.favorites

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 16-1-18
 */
class FavoritesViewModel : ViewModel() {
    private val repository = CryptoCoinRepository()
    private var mFavoriteCoinsList : LiveData<List<CryptoCoin>>?

    init {
        mFavoriteCoinsList = repository.getFavorites()
        refreshCryptoCoinList()
    }

    fun getFavoriteCoinsList() = mFavoriteCoinsList
    fun refreshCryptoCoinList() = repository.refreshCryptoCoinList()
}