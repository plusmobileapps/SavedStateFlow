package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    //    private val _query = MutableStateFlow<String>("") // Will not persist process death
    private val query = SavedStateFlow(
        savedStateHandle = savedStateHandle,
        key = "main-viewmodel-query-key",
        defaultValue = ""
    )

    private val _state = MutableStateFlow(
        State(
            isLoading = query.value.isNotBlank(),
            query = query.value,
            results = emptyList()
        )
    )
    val state: StateFlow<State> get() = _state

    init {
        observeQuery()
    }

    fun updateQuery(query: String) {
        _state.value = _state.value.copy(isLoading = true, query = query)
        this.query.value = query
    }

    private fun observeQuery() {
        viewModelScope.launch {
            query.asStateFlow()
                .flatMapLatest { query ->
                    NewsRepository.fetchQuery(query)
                }
                .collect { results ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        results = results
                    )
                }
        }
    }

    data class State(val isLoading: Boolean = false, val query: String, val results: List<String>)

}

object NewsRepository {
    fun fetchQuery(query: String): Flow<List<String>> = flow {
        delay(2000L)
        if (query.isEmpty()) return@flow
        emit(
            listOf(
                "Query: $query \nResult: result1",
                "Query: $query \nResult: result2",
                "Query: $query \nResult: result3"
            )
        )
    }
}