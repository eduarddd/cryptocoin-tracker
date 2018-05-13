package com.nightlydev.cryptocointracker.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
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
data class CryptoCoin(@PrimaryKey @ColumnInfo(name = "id") @SerializedName("short") var symbol: String,
                      @ColumnInfo(name = "name") @SerializedName("long") var name: String,
                      @SerializedName("price") var price: Double,
                      @SerializedName("cap24hrChange") var cap24hrChange: Double,
                      @ColumnInfo(name = "market_cap") @SerializedName("mktcap") var marketCap: Double) : Serializable {

    constructor() : this(symbol = "", name = "", price = 0.0, cap24hrChange = 0.0, marketCap = 0.0)
}