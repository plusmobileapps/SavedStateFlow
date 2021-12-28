package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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
    SavedStateFlowImpl(viewModelScope, SavedStateHandleWrapperImpl(savedStateHandle), key, defaultValue)

internal interface SavedStateHandleWrapper <T> {
    fun getValue(key: String): T?
    fun setValue(key: String, value: T)
    fun getFlow(key: String): Flow<T>
}

internal class SavedStateHandleWrapperImpl<T>(private val savedState: SavedStateHandle) : SavedStateHandleWrapper<T> {
    override fun getValue(key: String): T? = savedState.get(key)
    override fun setValue(key: String, value: T) = savedState.set(key, value)
    override fun getFlow(key: String): Flow<T> = savedState.getLiveData<T>(key).asFlow()
}

internal class SavedStateFlowImpl<T>(
    private val scope: CoroutineScope,
    private val savedStateHandle: SavedStateHandleWrapper<T>,
    private val key: String,
    defaultValue: T
) : SavedStateFlow<T> {
    private val _state: MutableStateFlow<T> =
        MutableStateFlow(savedStateHandle.getValue(key) ?: defaultValue)

    init {
        observeSavedState()
    }

    override var value: T
        get() = _state.value
        set(value) {
            savedStateHandle.setValue(key, value)
        }

    override fun asStateFlow(): StateFlow<T> = _state

    private fun observeSavedState() {
        scope.launch {
            savedStateHandle.getFlow(key).collect { value ->
                _state.value = value
            }
        }
    }

}