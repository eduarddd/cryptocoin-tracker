package com.nightlydev.cryptocointracker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**

 Json containing CryptoCoin info:

    {
        "cap24hrChange":3.34,
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
        "vwapDataBTC":16822.988935970203
    }

 * @author edu (edusevilla90@gmail.com)
 * @since 5-12-17
*/
@Entity
data class CryptoCoin(@PrimaryKey(autoGenerate = true)
                      var id: Long = 0,
                      var long: String = "",
                      var short: String = "",
                      var price: Double = 0.0,
                      var mktcap: Double = 0.0,
                      var supply: Double = 0.0,
                      var cap24hrChange: Double = 0.0) : Serializable