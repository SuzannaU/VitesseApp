package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.ui.CandidateUI

class FilterByNameUseCase {
    fun execute(candidates: List<CandidateUI>, filter: String?): List<CandidateUI> {
        return if (filter != null && filter.isNotEmpty()) {
            candidates.filter {
                it.firstname == filter || it.lastname == filter
            }
        } else {
            candidates
        }
    }
}