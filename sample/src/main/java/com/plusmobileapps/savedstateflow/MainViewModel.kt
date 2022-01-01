package com.plusmobileapps.savedstateflow

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MainViewModel(handle, NewsRepository) as T
    }
}

class MainViewModel(savedStateHandle: SavedStateHandle, private val newsDataSource: NewsDataSource) : ViewModel() {

    companion object {
        const val SAVED_STATE_QUERY_KEY = "main-viewmodel-query-key"
    }

    //    private val query = MutableStateFlow<String>("") // Will not persist process death
    private val query = SavedStateFlow(
        savedStateHandle = savedStateHandle,
        key = SAVED_STATE_QUERY_KEY,
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
                    newsDataSource.fetchQuery(query)
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