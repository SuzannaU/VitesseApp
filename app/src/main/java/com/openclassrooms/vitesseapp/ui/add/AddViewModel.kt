package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val saveCandidateUseCase: SaveCandidateUseCase,
    private val saveImageUseCase: SaveImageUseCase,
) : ViewModel() {

    private val _addUiState = MutableStateFlow<AddUiState>(AddUiState.LoadedState)
    val addUiState = _addUiState.asStateFlow()

    fun saveCandidate(candidateFormUI: CandidateFormUI) {
        viewModelScope.launch {
            val photoPath: String? = candidateFormUI.photoUri?.let {
                runCatching {
                    saveImageUseCase.execute(candidateFormUI.photoUri)
                }.onFailure {
                    _addUiState.value = AddUiState.ErrorState
                    return@launch
                }.getOrNull()
            }
            runCatching {
                saveCandidateUseCase.execute(candidateFormUI.toDomain(photoPath))
            }.onFailure {
                _addUiState.value = AddUiState.ErrorState
                return@launch
            }
        }
    }

    sealed class AddUiState {
        object LoadedState : AddUiState()
        object ErrorState : AddUiState()
    }
}