package com.nightlydev.cryptocointracker.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView
import com.nightlydev.cryptocointracker.R
import com.nightlydev.cryptocointracker.model.CryptoCoin

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 6-12-17
 */
class CryptoCoinIcon @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    init {
        val typeface = Typeface.createFromAsset(context.assets, "font/cryptocoins.ttf")
        setTypeface(typeface)
    }

    fun setCoin(coin: CryptoCoin) {
        text = coin.iconString(context)
        setTextColor(coin.iconColor(context))
    }
}

@ColorInt
fun CryptoCoin.iconColor(context: Context): Int {
    var iconColorId = context.resources.getIdentifier(shortName, "color", context.packageName)

    if (iconColorId <= 0) {
        iconColorId = context.resources.getIdentifier(longName, "color", context.packageName)
    }
    if (iconColorId <= 0 || iconColorId == 0xFFFFFF) {
        iconColorId = R.color.colorPrimary
    }
    try {
        return ContextCompat.getColor(context, iconColorId)
    } catch (exception: Resources.NotFoundException) {
         return ContextCompat.getColor(context, R.color.colorPrimary)
    }
}

fun CryptoCoin.iconString(context: Context): String {
    var iconSymbol = shortName
    var iconRestId = context.resources.getIdentifier(iconSymbol, "string", context.packageName)

    if (iconRestId <= 0) {
        iconSymbol += "_alt"
        iconRestId = context.resources.getIdentifier(iconSymbol, "string", context.packageName)
    }
    if (iconRestId > 0) {
        try {
            return context.getString(iconRestId)
        } catch (exception: Resources.NotFoundException) {
            return ""
        }
    } else {
        return ""
    }
}