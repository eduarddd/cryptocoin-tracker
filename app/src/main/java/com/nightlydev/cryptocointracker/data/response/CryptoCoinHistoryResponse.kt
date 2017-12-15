package com.nightlydev.cryptocointracker.data.response

/**
 * Created by edu on 14-12-17.
 */
data class CryptoCoinHistoryResponse(val market_cap: List<List<Any>>,
                                     val price: List<List<Any>>,
                                     val volume: List<List<Any>>)

data class CryptoCoinHistoryMarketCapItem(val date: Long, val value: Long)
data class CryptoCoinHistoryPriceItem(val date: Long, val value: Double)
data class CryptoCoinHistoryVolumeItem(val date: Long, val value: Long)