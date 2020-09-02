package com.ethanprentice.shopifywordsearch.util

import android.content.res.ColorStateList

object ColorUtil {
    fun getSingleColorStateList(color: Int): ColorStateList {
        val states: Array<IntArray> = arrayOf(IntArray(0))
        val colors = IntArray(1) { color }
        return ColorStateList(states, colors)
    }
}