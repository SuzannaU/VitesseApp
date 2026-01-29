package com.openclassrooms.vitesseapp.data.dao

import com.openclassrooms.vitesseapp.data.response.RateResponse
import retrofit2.Response
import retrofit2.http.GET

interface RateApiService {
    @GET("currency-api@latest/v1/currencies/eur.json")
    suspend fun getEuroRates(): Response<RateResponse>
}