package com.nightlydev.cryptocointracker.data

import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
 */
interface CryptoCoinService {

    @GET("front")
    fun listCryptoCoins(): Observable<List<CryptoCoin>>

    @GET("history/{day_count}day/{coin_symbol}")
    fun listCryptoCoinHistory(@Path("day_count") dayCount: Int,
                              @Path("coin_symbol") coinSymbol: String
    ): Observable<CryptoCoinHistoryResponse>

    @GET("history/{coin_symbol}")
    fun listCryptoCoinHistory(@Path("coin_symbol") coinSymbol: String
    ): Observable<CryptoCoinHistoryResponse>

    companion object Factory {
        fun create(): CryptoCoinService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://coincap.io/")
                    .build()

            return retrofit.create(CryptoCoinService::class.java)
        }
    }
}