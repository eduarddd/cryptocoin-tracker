package com.nightlydev.cryptocointracker.data

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 12-1-18
 */
class ViewModelFactory(val cryptoCoin: CryptoCoin) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoCoinViewModel::class.java)) {
            return CryptoCoinViewModel(cryptoCoin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}