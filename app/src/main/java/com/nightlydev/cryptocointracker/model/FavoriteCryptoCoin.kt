package com.nightlydev.cryptocointracker.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE


/**
 * @author edu (edusevilla90@gmail.com)
 * @since 9-1-18
 */
@Entity(tableName = "favoriteCryptoCoin",
        indices = [Index(value = ["crypto_coin_id"], unique = true)],
        foreignKeys = [
            ForeignKey(
                    entity = CryptoCoin::class,
                    parentColumns = ["id"],
                    childColumns = ["crypto_coin_id"],
                    onDelete = CASCADE)])
data class FavoriteCryptoCoin(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "crypto_coin_id") var cryptoCoinId: Long = 0)