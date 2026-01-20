package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.ui.CandidateUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {

    // State Useless?
    private var _uiState = MutableStateFlow<AddUiState>(AddUiState.DefaultState)
    val uiState = _uiState.asStateFlow()

    fun saveCandidate(candidate: CandidateUI) {
        viewModelScope.launch {
            saveCandidateUseCase.execute(candidate)
        }
    }

    sealed class AddUiState() {  // sealed interface?
        object DefaultState : AddUiState()
        // No error state?
    }
}