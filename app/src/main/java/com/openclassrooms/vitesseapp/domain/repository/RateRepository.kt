package com.openclassrooms.vitesseapp.domain.repository

import java.math.BigDecimal

interface RateRepository {

    suspend fun fetchRatesForEur(): BigDecimal
}