package com.nightlydev.cryptocointracker.extensions

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * @author edu (edusevilla90@gmail.com)
 * @since 3-5-18
 */
fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}