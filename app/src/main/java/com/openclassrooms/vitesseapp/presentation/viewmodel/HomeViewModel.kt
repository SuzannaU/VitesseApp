package com.openclassrooms.vitesseapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.presentation.DispatcherProvider
import com.openclassrooms.vitesseapp.presentation.BitmapDecoder
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.filterByName
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase,
    private val bitmapDecoder: BitmapDecoder,
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val homeStateFlow = _homeStateFlow.asStateFlow()
    private var allCandidates = emptyList<CandidateDisplay>()

    fun loadAllCandidates() {
        viewModelScope.launch {
            _homeStateFlow.value = HomeUiState.LoadingState
            withContext(dispatcherProvider.io) {
                loadAllCandidatesUseCase.execute()
            }
                .catch {
                    _homeStateFlow.value = HomeUiState.ErrorState
                    return@catch
                }
                .collect { loadedCandidates ->
                    if (loadedCandidates.isEmpty()) {
                        _homeStateFlow.value = HomeUiState.NoCandidateFound
                    } else {
                        allCandidates =
                            loadedCandidates.map { candidate -> candidate.toCandidateDisplay(null, bitmapDecoder) }
                        _homeStateFlow.value = HomeUiState.CandidatesFound(allCandidates)
                    }
                }
        }
    }

    fun loadFavoritesTab() : List<CandidateDisplay> {
        if(_homeStateFlow.value is HomeUiState.CandidatesFound) {
            val currentCandidates = (_homeStateFlow.value as HomeUiState.CandidatesFound).candidates
            return currentCandidates.filter { it.isFavorite }
        }
        return emptyList()
    }

    fun loadFilteredCandidates(searchedText: String?) {
        viewModelScope.launch {
            if (searchedText.isNullOrBlank()) {
                _homeStateFlow.value = HomeUiState.CandidatesFound(allCandidates)
                return@launch
            }
            val filteredCandidates = allCandidates.filterByName(searchedText)
            if (filteredCandidates.isEmpty()) {
                _homeStateFlow.value = HomeUiState.NoCandidateFound
            } else {
                _homeStateFlow.value =
                    HomeUiState.CandidatesFound(filteredCandidates)
            }
        }
    }

    sealed class HomeUiState {
        object LoadingState : HomeUiState()
        object NoCandidateFound : HomeUiState()
        object ErrorState : HomeUiState()

        data class CandidatesFound(
            val candidates: List<CandidateDisplay>
        ) : HomeUiState()
    }
}