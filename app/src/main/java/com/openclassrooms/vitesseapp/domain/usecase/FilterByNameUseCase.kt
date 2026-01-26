package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.model.Candidate

class FilterByNameUseCase {
    fun execute(candidates: List<Candidate>, filter: String?): List<Candidate> {
        return if (filter != null && filter.isNotEmpty()) {
            candidates.filter {
                it.firstname == filter || it.lastname == filter
            }
        } else {
            candidates
        }
    }
}