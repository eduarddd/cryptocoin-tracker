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
        "longName":"Bitcoin",
        "mktcap":284446734242.4,
        "perc":3.34,
        "price":16990.2,
        "shapeshift":true,
        "shortName":"BTC",
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
data class CryptoCoin(@PrimaryKey @ColumnInfo(name = "id") @SerializedName("short") var shortName: String,
                      @ColumnInfo(name = "long_name") @SerializedName("long") var longName: String,
                      @SerializedName("price") var price: Double,
                      @ColumnInfo(name = "market_cap") @SerializedName("mktcap") var mktcap: Double,
                      @SerializedName("supply") var supply: Double,
                      @SerializedName("cap24hrChange") var cap24hrChange: Double) : Serializable {

    constructor() : this("", "", 0.0, 0.0, 0.0, 0.0)
}