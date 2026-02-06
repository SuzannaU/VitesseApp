package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.dao.RateApiService
import com.openclassrooms.vitesseapp.data.repository.RateRepositoryImpl
import com.openclassrooms.vitesseapp.data.response.RateResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import retrofit2.Response
import kotlin.test.assertEquals

class RateRepositoryImplTest {

    private val rateApiService  = mockk<RateApiService>()
    private val repository = RateRepositoryImpl(rateApiService = rateApiService)

    @Test
    fun fetchRatesForEur_shouldCallApiAndOnlyReturnGbpRate() = runTest {
        val gbpRate = 1.50

        val rateResponse = RateResponse(
            date =  "2026-01-04",
            rates = mapOf(
                "1inch" to 0.50,
                "aave" to 0.007148,
                "gbp" to gbpRate,
            )
        )
        val response = Response.success(rateResponse)
        coEvery { rateApiService.getEuroRates() } returns response

        val result = repository.fetchRatesForEur()
        assertNotNull(result)
        assertEquals(gbpRate, result.toDouble(), 0.01)

        coVerify { rateApiService.getEuroRates() }
    }

    @Test
    fun fetchRatesForEur_withFailedResponse_shouldCallApiAndReturnNull() = runTest {

        val body = """[]""".toResponseBody("application/json".toMediaType())

        val response = Response.error<RateResponse>(400, body)
        coEvery { rateApiService.getEuroRates() } returns response

        val result = repository.fetchRatesForEur()
        assertNull(result)

        coVerify { rateApiService.getEuroRates() }
    }
}