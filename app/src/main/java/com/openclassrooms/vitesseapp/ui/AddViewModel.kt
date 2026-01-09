package com.openclassrooms.vitesseapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow<AddUiState>(AddUiState())
    val uiState = _uiState.asStateFlow()

    fun saveCandidate(candidate: Candidate) {
        viewModelScope.launch {
            saveCandidateUseCase.execute(candidate)
        }
    }

    class AddUiState()
}