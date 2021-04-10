package com.free.grocerycoupons

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

var addLimit = 0

fun getDate(date: String): Date {
    val formatIn = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
    val formatOut = SimpleDateFormat("EEE, MMM dd HH:mm:ss yyyy")
    var formattedDate: Date? = null
    try {
        formattedDate = formatIn.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate!!
}
