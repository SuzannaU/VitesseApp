package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay

class FilterByNameUseCase {
    fun execute(candidates: List<CandidateDisplay>, filter: String?): List<CandidateDisplay> {
        return if (filter != null && filter.isNotEmpty()) {
            candidates.filter {
                it.firstname == filter || it.lastname == filter
            }
        } else {
            candidates
        }
    }
}