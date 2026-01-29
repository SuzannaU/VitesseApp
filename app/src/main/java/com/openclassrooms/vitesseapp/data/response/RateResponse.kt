package com.openclassrooms.vitesseapp.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RateResponse(
    @Json(name = "date")
    val date: String,
    @Json(name = "eur")
    val rates: Map<String, Double>,
)
