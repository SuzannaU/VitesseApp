package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow<AddUiState>(AddUiState())
    val uiState = _uiState.asStateFlow()

    fun saveCandidate(candidate: CandidateDto) {
        viewModelScope.launch {
            saveCandidateUseCase.execute(candidate)
        }
    }

    class AddUiState()
}