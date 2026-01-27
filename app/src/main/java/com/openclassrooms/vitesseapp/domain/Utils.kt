package com.openclassrooms.vitesseapp.domain

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

fun calculateAge(birthdate: Long): Int {
    val birthdateLocalDate = Instant.ofEpochMilli(birthdate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return Period.between(birthdateLocalDate, LocalDate.now()).years
}

fun createBirthdateForAge(age: Int): Long {
    return LocalDate.now()
        .minusYears(age.toLong())
        .atStartOfDay(ZoneId.of("UTC"))
        .toInstant()
        .toEpochMilli()
}