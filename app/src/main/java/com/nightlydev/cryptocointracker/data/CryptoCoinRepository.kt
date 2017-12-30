package com.nightlydev.cryptocointracker.data

import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Observable

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinRepository {

    private val cryptoCoinService: CryptoCoinService = CryptoCoinService.create()

    fun listCryptoCoins(): Observable<List<CryptoCoin>> {
        return cryptoCoinService.listCryptoCoins()
    }

    fun listCryptoCoinHistory(dayCount: Int,
                              coinSymbol: String): Observable<CryptoCoinHistoryResponse> {
        return if (dayCount > 0) {
            cryptoCoinService.listCryptoCoinHistory(dayCount, coinSymbol)
        } else {
            cryptoCoinService.listCryptoCoinHistory(coinSymbol)
        }
    }
}