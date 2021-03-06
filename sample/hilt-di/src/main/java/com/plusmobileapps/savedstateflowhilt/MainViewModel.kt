package com.plusmobileapps.savedstateflowhilt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plusmobileapps.savedstateflow.SavedStateFlow
import com.plusmobileapps.savedstateflow.SavedStateFlowHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateFlowHandle: SavedStateFlowHandle,
    private val newsDataSource: NewsDataSource
) : ViewModel() {

    companion object {
        const val SAVED_STATE_QUERY_KEY = "main-viewmodel-query-key"
    }

    private val query: SavedStateFlow<String> =
        savedStateFlowHandle.getSavedStateFlow(viewModelScope, SAVED_STATE_QUERY_KEY, "")

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