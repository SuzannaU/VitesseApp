package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.repository.RateRepository
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import java.math.BigDecimal

class ConvertEurToGbpUseCaseTest {

    private val rateRepository = mockk<RateRepository>()
    private val convertEurToGbpUseCase = ConvertEurToGbpUseCase(rateRepository)

    @Test
    fun execute_shouldCallRepositoryAndReturnRate() = runTest {

        val rate = BigDecimal(1.50)
        val amountEur = 100L
        val expectedAmountGbp = BigDecimal.valueOf(amountEur).multiply(rate).toLong()
        coEvery { rateRepository.fetchRatesForEur() } returns rate

        val result = convertEurToGbpUseCase.execute(amountEur)

        assertEquals(expectedAmountGbp, result)
        coVerify { rateRepository.fetchRatesForEur() }
    }

    @Test
    fun execute_withNullRate_shouldCallRepositoryAndReturnNull() = runTest {

        val rate = null
        val amountEur = 100L
        coEvery { rateRepository.fetchRatesForEur() } returns rate

        val result = convertEurToGbpUseCase.execute(amountEur)

        assertNull(result)
        coVerify { rateRepository.fetchRatesForEur() }
    }
}