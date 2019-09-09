package com.vit.demomlkit.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.lang.Double.parseDouble

fun String.currencyFormat(): String {
    var s = replace(",", "")
        .replace("d", "")
        .replace("D", "")


    if (s.split('.').last().length < 3){
        val lastDot = s.lastIndexOf('.')
        if (lastDot != -1) s = s.removeRange(lastDot, s.length)
    }

    return s.replace(".", "")
}

fun String.isDouble(): Boolean {
    try {
        val num = parseDouble(this)
    } catch (e: NumberFormatException) {
        return false
    }
    return true
}

fun String.isCurrency(): Boolean {
    if (!contains(".") && !contains(",")) return false
    val s = currencyFormat()
    if (s.length > 8 || s.length < 4) return false
    return s.isDouble()
}

fun ArrayList<FirebaseVisionText.Element>.getTotalPay(): Int {
    val prices = ArrayList<Int>()
    forEach {
        try {
            prices.add(it.text.currencyFormat().toInt())
        } catch (e: Exception) {
        }
    }
    val lastMax: Int = prices.lastIndexOf(prices.max())
    val firstMax: Int = prices.indexOf(prices.max())

    if (lastMax - firstMax == 3) return prices[lastMax - 1]

    val listTemp = ArrayList<Int>().apply {
        for (i in 0 until lastMax) {
            add(prices[i])
        }
    }
    return listTemp.max() ?: 0
}

inline fun postDelay(time: Long = 0, crossinline block: () -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({ block() }, time)
}