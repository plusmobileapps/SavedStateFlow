package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
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

/**
 * Builder function for creating a [SavedStateFlow]
 *
 * @param savedStateHandle
 * @param key
 * @param defaultValue
 */
fun <T : Any> ViewModel.SavedStateFlow(savedStateHandle: SavedStateHandle, key: String, defaultValue: T): SavedStateFlow<T> =
    SavedStateFlowImpl(viewModelScope, savedStateHandle, key, defaultValue)

internal class SavedStateFlowImpl<T>(
    private val scope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
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
            savedStateHandle.set(key, value)
        }

    override fun asStateFlow(): StateFlow<T> = _state

    private fun observeSavedState() {
        scope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect { value ->
                _state.value = value
            }
        }
    }

}