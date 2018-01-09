package com.nightlydev.cryptocointracker.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE


/**
 * @author edu (edusevilla90@gmail.com)
 * @since 9-1-18
 */
@Entity(tableName = "favoriteCryptoCoin",
        indices = [Index(value = ["cryptoCoinId"], unique = true)],
        foreignKeys = [
            ForeignKey(
                    entity = CryptoCoin::class,
                    parentColumns = ["id"],
                    childColumns = ["cryptoCoinId"],
                    onDelete = CASCADE)])
data class FavoriteCryptoCoin(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "cryptoCoinId") var cryptoCoinId: Long = 0)