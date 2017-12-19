package com.nightlydev.cryptocointracker.data.response

import java.util.*

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 15-12-17
 */
data class CryptoCoinHistoryResponse(val market_cap: List<List<Any>>,
                                     val price: List<List<Any>>,
                                     val volume: List<List<Any>>)

data class CryptoCoinHistoryMarketCapItem(val date: Date, val value: Long)
data class CryptoCoinHistoryPriceItem(val date: Date, val value: Double)
data class CryptoCoinHistoryVolumeItem(val date: Date, val value: Long)

fun CryptoCoinHistoryResponse.priceHistory(): List<CryptoCoinHistoryPriceItem> {
    val priceHistory: MutableList<CryptoCoinHistoryPriceItem> = mutableListOf()

    for (item in price) {
        val date = (item[0] as Double).toLong()
        val value = item[1] as Double
        val priceItem = CryptoCoinHistoryPriceItem(Date(date), value)
        priceHistory.add(priceItem)
    }

    return priceHistory
}