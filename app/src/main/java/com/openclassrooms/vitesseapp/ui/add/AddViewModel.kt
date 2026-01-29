package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toDomain
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
    private val saveImageUseCase: SaveImageUseCase,
) : ViewModel() {

    fun saveCandidate(candidateFormUI: CandidateFormUI) {
        viewModelScope.launch {
            val photoPath = candidateFormUI.photoUri?.let {
                saveImageUseCase.execute(candidateFormUI.photoUri)
            }
            saveCandidateUseCase.execute(candidateFormUI.toDomain(photoPath))
        }
    }
}