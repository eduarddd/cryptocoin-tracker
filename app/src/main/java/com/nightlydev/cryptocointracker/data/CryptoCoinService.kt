package com.nightlydev.cryptocointracker.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nightlydev.cryptocointracker.BuildConfig
import com.nightlydev.cryptocointracker.data.response.CryptoCoinHistoryResponse
import com.nightlydev.cryptocointracker.model.CryptoCoin
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

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
            val retrofit = provideRetrofit()

            return retrofit.create(CryptoCoinService::class.java)
        }
    }
}

private fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .baseUrl("https://coincap.io/")
            .client(provideOkHttpClient())
            .build()
}

private fun provideOkHttpClient(): OkHttpClient {
    val httpClient = OkHttpClient.Builder()

    httpClient.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
    httpClient.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
    httpClient.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        httpClient.addInterceptor(provideHttpLoggingInterceptor())
    }

    return httpClient.build()
}

private fun provideGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)

    return gsonBuilder.create()
}

private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpInterceptor = HttpLoggingInterceptor()
    httpInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpInterceptor
}

private const val DEFAULT_TIMEOUT = 30