package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

//    private val _query = MutableStateFlow<String>("") // Will not persist process death
    private val _query = SavedStateFlow(
        savedStateHandle = savedStateHandle,
        key = "main-viewmodel-query-key",
        initialValue = ""
    )
    val query: StateFlow<String> = _query.asStateFlow()

    private val _newsArticles = MutableStateFlow(emptyList<String>())
    val newsArticles: StateFlow<List<String>> = _newsArticles

    private val _isLoading = MutableStateFlow(query.value.isNotBlank())
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        observeQuery()
    }

    fun queryUpdated(query: String) {
        _isLoading.value = true
        this._query.value = query
    }

    private fun observeQuery() {
        viewModelScope.launch {
            query.flatMapLatest(NewsRepository::fetchQuery).collect {
                _newsArticles.value = it
                _isLoading.value = false
            }
        }
    }

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