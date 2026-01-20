package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.CandidateUI
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
    private val saveImageUseCase: SaveImageUseCase,
) : ViewModel() {

    fun saveCandidate(candidateUI: CandidateUI) {
        viewModelScope.launch {
            val photoPath = candidateUI.photoUri?.let {
                saveImageUseCase.execute(candidateUI.photoUri)
            }
            saveCandidateUseCase.execute(candidateUI.toDomain(photoPath))
        }
    }
}