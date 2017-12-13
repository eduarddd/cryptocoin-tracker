package com.nightlydev.cryptocointracker

import android.content.Context
import java.io.Serializable

/**
{
"id": "bitcoin",
"name": "Bitcoin",
"symbol": "BTC",
"rank": "1",
"price_usd": "11971.5",
"price_btc": "1.0",
"24h_volume_usd": "6638460000.0",
"market_cap_usd": "200185627275",
"available_supply": "16721850.0",
"total_supply": "16721850.0",
"max_supply": "21000000.0",
"percent_change_1h": "0.1",
"percent_change_24h": "4.77",
"percent_change_7d": "19.29",
"last_updated": "1512487753",
"price_eur": "10091.375925",
"24h_volume_eur": "5595889857.0",
"market_cap_eur": "168746474511"
},
 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
*/
data class CryptoCoin(val id: String,
                      val name: String,
                      val symbol: String,
                      val rank: Int,
                      val price_usd: Double,
                      val _24h_volume_usd: Double,
                      val market_cap_usd: Double,
                      val available_supply: Double,
                      val total_supply: Double,
                      val percent_change_1h: Double,
                      val percent_change_24h: Double,
                      val percent_change_7d: Double,
                      val last_updated: Long,
                      val price_eur: Double) : Serializable {
    fun iconColor(context: Context): Int {
        var iconColorId = context.resources.getIdentifier(symbol, "color", context.packageName)

        if (iconColorId <= 0) {
            iconColorId = context.resources.getIdentifier(name, "color", context.packageName)
        }
        return iconColorId
    }

    fun icon(context: Context): Int {
        var iconRestId = context.resources.getIdentifier(symbol, "string", context.packageName)

        if (iconRestId <= 0) {
            iconRestId = context.resources.getIdentifier(name, "string", context.packageName)
        }
        return iconRestId
    }
}