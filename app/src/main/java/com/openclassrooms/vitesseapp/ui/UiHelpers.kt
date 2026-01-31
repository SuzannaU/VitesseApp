package com.openclassrooms.vitesseapp.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatBirthdateToString(birthdate: Long) : String {
    val locale = Locale.getDefault()
    val pattern = when (locale.language) {
        "fr" -> "dd/MM/yyyy"
        "en" -> "MM/dd/yyyy"
        else -> "dd/MM/yyyy"
    }

    val simpleDateFormat = SimpleDateFormat(pattern, locale)
    val date = Date(birthdate)
    return simpleDateFormat.format(date)
}

fun formatSalaryToString(salaryInCents: Long) : String {
    val locale = Locale.getDefault()

    val string = String.format(locale,"%.2f", salaryInCents.toDouble()/100)
    return string
}