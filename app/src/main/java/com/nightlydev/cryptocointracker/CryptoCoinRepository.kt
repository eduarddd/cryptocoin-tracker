package com.nightlydev.cryptocointracker

import io.reactivex.Observable

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
class CryptoCoinRepository(val apiService: CryptoCoinService) {

    fun listCryptoCoins(): Observable<List<CryptoCoin>> {
        return apiService.listCryptoCoins()
    }
}