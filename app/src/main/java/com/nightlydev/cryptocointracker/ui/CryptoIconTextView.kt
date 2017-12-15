package com.nightlydev.cryptocointracker.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 6-12-17
 */
class CryptoIconTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    init {
        val typeface = Typeface.createFromAsset(context.assets, "font/cryptocoins.ttf")
        setTypeface(typeface)
    }
}