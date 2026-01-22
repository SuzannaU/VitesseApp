package com.openclassrooms.vitesseapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase
) : ViewModel() {

    private val _candidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())
    val candidateFlow = _candidatesFlow.asStateFlow()

    fun loadAllCandidates() {
        viewModelScope.launch {
            loadAllCandidatesUseCase.execute()
                .collect {
                _candidatesFlow.value = it
            }
        }
    }
}