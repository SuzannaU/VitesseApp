package com.openclassrooms.vitesseapp.presentation.model

fun List<CandidateDisplay>.filterByName(searchText: String): List<CandidateDisplay> {
    return filter {
        it.firstname.equals(searchText, ignoreCase = true) ||
        it.lastname.equals(searchText, ignoreCase = true)
    }
}