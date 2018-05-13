package com.nightlydev.cryptocointracker.cryptoCoinDetail

import android.arch.lifecycle.*
import com.google.firebase.database.FirebaseDatabase
import com.nightlydev.cryptocointracker.data.CryptoCoinRepository
import com.nightlydev.cryptocointracker.data.Resource
import com.nightlydev.cryptocointracker.data.Status
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryPriceItem
import com.nightlydev.cryptocointracker.model.Alert
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 11-1-18
 */
class CryptoCoinViewModel(cryptoCoinId: String, displayHistoryPeriod: Int) : ViewModel() {
    private val repository = CryptoCoinRepository()
    private var cryptoCoinId = MutableLiveData<String>()
    var cryptoCoin : LiveData<CryptoCoin>
    var displayHistoryPeriod = MutableLiveData<Int>()
    var cryptoCoinHistory = MediatorLiveData<Resource<List<CryptoCoinHistoryPriceItem>?>>()
    var isFavorite : LiveData<Boolean>

    init {
        this.cryptoCoinId.value = cryptoCoinId
        this.displayHistoryPeriod.value = displayHistoryPeriod

        cryptoCoin = Transformations.switchMap(this.cryptoCoinId) {
            id -> repository.getCryptoCoin(id)
        }
        cryptoCoinHistory.addSource(this.cryptoCoin) { _ ->
            cryptoCoinHistory.addSource(this.displayHistoryPeriod) { _ ->
                val cryptoCoinHistorySource = getCryptoCoinHistorySource()
                cryptoCoinHistory.addSource(cryptoCoinHistorySource) { history ->
                    cryptoCoinHistory.value = history
                    when (history?.status) {
                        Status.SUCCESS, Status.ERROR -> cryptoCoinHistory.removeSource(cryptoCoinHistorySource)
                        else -> {} //ignore
                    }
                }
            }
        }
        isFavorite = Transformations.map(repository.getFavorites()!!) {
            favorites -> (favorites.contains(cryptoCoin.value))
        }
    }

    private fun getCryptoCoinHistorySource(): LiveData<Resource<List<CryptoCoinHistoryPriceItem>?>> {
        return repository.getCryptoCoinPriceHistory(displayHistoryPeriod.value!!, cryptoCoin.value!!.symbol)
    }

    fun saveFavorite() {
        if (!isFavorite.value!!) {
            repository.saveFavorite(cryptoCoin.value)
        } else {
            repository.removeFavorite(cryptoCoin.value!!)
        }
    }

    fun createAlert(cryptoCoin: CryptoCoin,
                    price: Double,
                    note: String = "",
                    persistent: Boolean = false) {
        val alert = Alert(cryptoCoin, price, note, persistent)

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("alerts").push().setValue(alert)
    }
}