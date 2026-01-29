package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.RateRepository
import java.math.BigDecimal

class ConvertEurToGbpUseCase(private val rateRepository: RateRepository) {

    suspend fun execute(amountEur: Long?) : Long? {
        amountEur?.let {
            val rate = rateRepository.fetchRatesForEur()
            val amountGbp = BigDecimal.valueOf(amountEur).multiply(rate)
            return amountGbp.toLong()
        }
        return null
    }
}