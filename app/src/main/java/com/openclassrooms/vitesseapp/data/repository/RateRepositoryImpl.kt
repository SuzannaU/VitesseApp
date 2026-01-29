package com.openclassrooms.vitesseapp.data.repository

import com.openclassrooms.vitesseapp.domain.repository.RateRepository
import java.math.BigDecimal
import java.math.RoundingMode

class RateRepositoryImpl : RateRepository {
    override suspend fun fetchRatesForEur(): BigDecimal {
        val rate = 7.442817
        return BigDecimal.valueOf(rate).setScale(2, RoundingMode.UP)
    }
}