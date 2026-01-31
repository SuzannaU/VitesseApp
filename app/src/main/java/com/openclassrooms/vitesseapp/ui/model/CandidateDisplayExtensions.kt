package com.openclassrooms.vitesseapp.ui.model

fun List<CandidateDisplay>.filterByName(filter: String?): List<CandidateDisplay> {
    if(filter.isNullOrEmpty()) return this
    return filter { it.firstname == filter || it.lastname == filter }
}