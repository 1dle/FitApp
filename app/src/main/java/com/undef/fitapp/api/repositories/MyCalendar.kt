package com.undef.fitapp.api.repositories

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object MyCalendar {
    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    val today = calendar.time

    private val sdfTime = SimpleDateFormat("HH:mm", Locale("HU"))
    private val sdfDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("HU"))
    private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale("HU"))

    //HH:mm
    @SuppressLint("SimpleDateFormat")
    fun getHourAndMinutes(): String{
        sdfTime.timeZone = TimeZone.getTimeZone("GMT");
        return sdfTime.format(calendar.time)
    }

    fun dateToString(date: Date)= sdfDate.format(date)

    fun getCurrentDate() = calendar.time

    fun dateTimeToString(dateTime:Date) = sdfDateTime.format(dateTime)
}
//for sorting
@SuppressLint("SimpleDateFormat")
fun String.toDateTime(): Date{
    return this.let{
        val sdfDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("HU"))
        sdfDateTime.parse(it)!!
    }
}

fun Date.toCalendar(): Calendar? {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}