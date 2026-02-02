package com.openclassrooms.vitesseapp.ui.model

fun List<CandidateDisplay>.filterByName(searchText: String): List<CandidateDisplay> {
    return filter {
        it.firstname.equals(searchText, ignoreCase = true) ||
        it.lastname.equals(searchText, ignoreCase = true)
    }
}