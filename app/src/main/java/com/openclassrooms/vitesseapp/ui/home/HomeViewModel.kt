package com.openclassrooms.vitesseapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val homeStateFlow = _homeStateFlow.asStateFlow()
    var allCandidates = emptyList<Candidate>()

    fun loadAllCandidates() {
        viewModelScope.launch {
            delay(1000)
            loadAllCandidatesUseCase.execute()
                .collect { loadedCandidates ->
                    if (loadedCandidates.isEmpty()) {
                        _homeStateFlow.value = HomeUiState.NoCandidateFound
                    } else {
                        allCandidates = loadedCandidates
                        _homeStateFlow.value = HomeUiState.CandidatesFound(loadedCandidates)
                    }
                }
        }
    }

    fun loadSearchedCandidates(searchedText: String?) {
        _homeStateFlow.value = HomeUiState.LoadingState
        viewModelScope.launch {
            delay(1000)
            if (searchedText != null && searchedText.isNotEmpty()) {
                val filteredCandidates = allCandidates.filter {
                    it.firstname == searchedText || it.lastname == searchedText
                }
                if (filteredCandidates.isEmpty()) {
                    _homeStateFlow.value = HomeUiState.NoCandidateFound
                } else {
                    _homeStateFlow.value =
                        HomeUiState.CandidatesFound(filteredCandidates)
                }
            } else {
                _homeStateFlow.value = HomeUiState.CandidatesFound(allCandidates)
            }
        }
    }

    sealed class HomeUiState {
        object LoadingState : HomeUiState()

        data class CandidatesFound(
            val candidates: List<Candidate>
        ) : HomeUiState()

        object NoCandidateFound : HomeUiState()
    }
}