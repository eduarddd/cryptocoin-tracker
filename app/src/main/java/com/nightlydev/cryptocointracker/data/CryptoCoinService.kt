package com.nightlydev.cryptocointracker.data

import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
interface CryptoCoinService {

    @GET("ticker/?convert=EUR&limit=50")
    fun listCryptoCoins(): Observable<List<CryptoCoin>>

    companion object Factory {
        fun create(): CryptoCoinService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.coinmarketcap.com/v1/")
                    .build()

            return retrofit.create(CryptoCoinService::class.java)
        }
    }
}