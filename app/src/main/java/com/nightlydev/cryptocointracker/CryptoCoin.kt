package com.nightlydev.cryptocointracker

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
 *
 * Created by eduardo on 12/5/17.
 */
data class CryptoCoin(val id: String,
                      val name: String,
                      val symbol: String,
                      val rank: Int,
                      val price_usd: Double)