package com.nightlydev.cryptocointracker.data

import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by edu on 14-12-17.
 */
interface CryptoCoinHistoryService {

    @GET("history/{day_count}day/{coin_symbol}")
    fun listCryptoCoinHistory(@Path("day_count") dayCount: Int,
                              @Path("coin_symbol") coinSymbol: String
    ): Observable<CryptoCoinHistoryResponse>

    companion object Factory {
        fun create(): CryptoCoinHistoryService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://www.coincap.io/")
                    .build()

            return retrofit.create(CryptoCoinHistoryService::class.java)
        }
    }
}