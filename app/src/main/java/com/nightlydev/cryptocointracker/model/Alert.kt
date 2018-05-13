package com.nightlydev.cryptocointracker.model

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 13-5-18
 */
data class Alert(val coin: CryptoCoin,
                 val price: Double,
                 val note: String = "",
                 val persistent: Boolean = false)