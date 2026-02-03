package com.openclassrooms.vitesseapp.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toCandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.toCandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val loadCandidateUseCase: LoadCandidateUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {

    private val _editUiState = MutableStateFlow<EditUiState>(EditUiState.LoadingState)
    val editUiState = _editUiState.asStateFlow()
    private var loadedCandidate: Candidate? = null

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            _editUiState.value = EditUiState.LoadingState

            runCatching {
                val candidate = loadCandidateUseCase.execute(candidateId) ?: return@runCatching null

                candidate to candidate.toCandidateFormUI()
            }.onSuccess { result ->
                result?.let { (candidate, candidateUI) ->
                    loadedCandidate = candidate
                    _editUiState.value = EditUiState.CandidateFound(candidateUI)
                } ?: run {
                    loadedCandidate = null
                    _editUiState.value = EditUiState.NoCandidateFound
                }
            }.onFailure {
                loadedCandidate = null
                _editUiState.value = EditUiState.ErrorState
            }
        }
    }

    fun saveCandidate(candidateFormUI: CandidateFormUI) {
        viewModelScope.launch {
            val photoPath: String? = candidateFormUI.photoUri?.let {
                runCatching {
                    saveImageUseCase.execute(candidateFormUI.photoUri)
                }.onFailure {
                    _editUiState.value = EditUiState.ErrorState
                    return@launch
                }.getOrNull()
            }
            runCatching {
                saveCandidateUseCase.execute(candidateFormUI.toDomain(photoPath))
            }.onFailure {
                _editUiState.value = EditUiState.ErrorState
                return@launch
            }
        }
    }

    sealed class EditUiState{
        object LoadingState : EditUiState()
        object NoCandidateFound : EditUiState()
        object ErrorState : EditUiState()

        data class CandidateFound(
            val candidateFormUI: CandidateFormUI
        ) : EditUiState()
    }
}