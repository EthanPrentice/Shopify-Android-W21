package com.ethanprentice.shopifywordsearch.util

import android.content.res.Resources.getSystem

/**
 * Converts px to dp
 */
val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

/**
 * Converts dp to px
 */
val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()
