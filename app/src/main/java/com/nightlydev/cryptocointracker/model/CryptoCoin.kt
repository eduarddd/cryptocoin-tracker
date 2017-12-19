package com.nightlydev.cryptocointracker.model

import android.content.Context
import com.nightlydev.cryptocointracker.R
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

{"cap24hrChange":3.34,
"long":"Bitcoin",
"mktcap":284446734242.4,
"perc":3.34,
"price":16990.2,
"shapeshift":true,
"short":"BTC",
"supply":16741812,
"usdVolume":14067700000,
"volume":14067700000,
"vwapData":16822.988935970203,
"vwapDataBTC":16822.988935970203}

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
                      val price_eur: Double) : Serializable