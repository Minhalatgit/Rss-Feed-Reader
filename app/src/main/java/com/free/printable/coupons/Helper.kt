package com.free.printable.coupons

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

var adLimit = 0

const val AD_COUNTER = 3

fun getDate(date: String): Date {
    val formatIn = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
    var formattedDate: Date? = null
    try {
        formattedDate = formatIn.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate!!
}

fun getDateString(date: Date): String {
    val format = SimpleDateFormat("EEE, dd MMM yyyy @ HH:mm")
    return format.format(date)
}

fun getDateString(date: Long): String {
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy @ HH:mm")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    return formatter.format(calendar.time)
}

fun getDate(date: Long): Date {
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy @ HH:mm")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    val dateInString = formatter.format(calendar.time)

    var formattedDate: Date? = null
    try {
        formattedDate = formatter.parse(dateInString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate!!
}
