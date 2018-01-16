package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.nightlydev.cryptocointracker.cryptoCoinDetail.CryptoCoinViewModel

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 12-1-18
 */
class ViewModelFactory(private val cryptoCoinId: String,
                       private val displayHistoryPeriod: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoCoinViewModel::class.java)) {
            return CryptoCoinViewModel(cryptoCoinId, displayHistoryPeriod) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}