package com.openclassrooms.vitesseapp.data.repository

import com.openclassrooms.vitesseapp.data.dao.RateApiService
import com.openclassrooms.vitesseapp.domain.repository.RateRepository
import java.math.BigDecimal
import java.math.RoundingMode

class RateRepositoryImpl(
    private val rateApiService: RateApiService
) : RateRepository {
    override suspend fun fetchRatesForEur(): BigDecimal? {
        val response = rateApiService.getEuroRates()
        val gbpRate = response.body()?.rates?.get("gbp")
        if (response.isSuccessful && gbpRate != null) {
            return BigDecimal.valueOf(gbpRate).setScale(2, RoundingMode.UP)
        }
        return null
    }
}