package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A [StateFlow] wrapper around [SavedStateHandle.getLiveData].
 */
interface SavedStateFlow<T> {

    var value: T

    fun asStateFlow(): StateFlow<T>

}

internal class SavedStateFlowImpl<T>(
    private val viewModelScope: CoroutineScope,
    private val savedStateHandle: SavedStateFlowHandle,
    private val key: String,
    defaultValue: T
) : SavedStateFlow<T> {
    private val _state: MutableStateFlow<T> =
        MutableStateFlow(savedStateHandle.get<T>(key) ?: defaultValue)

    init {
        observeSavedState()
    }

    override var value: T
        get() = _state.value
        set(value) {
            savedStateHandle[key] = value
        }

    override fun asStateFlow(): StateFlow<T> = _state

    private fun observeSavedState() {
        viewModelScope.launch {
           savedStateHandle.getFlow<T>(key).collect {
                _state.value = it
            }
        }
    }

}