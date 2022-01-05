package com.plusmobileapps.savedstateflow

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper around [androidx.lifecycle.SavedStateHandle] with added support for returning
 * values directly as a [Flow] or [SavedStateFlow]
 */
interface SavedStateFlowHandle {

    /**
     * @see [SavedStateHandle.get]
     */
    @MainThread
    operator fun <T> get(key: String): T?

    /**
     * @see [SavedStateHandle.set]
     */
    @MainThread
    operator fun <T> set(key: String, value: T)

    /**
     * Returns a [SavedStateFlow] with the provided [key] observing any changes with the [viewModelScope].
     * If no value exists for the key using [SavedStateFlowHandle.get], then the [defaultValue] will
     * be the starting value in the underlying [kotlinx.coroutines.flow.StateFlow]
     */
    @MainThread
    fun <T> getSavedStateFlow(viewModelScope: CoroutineScope, key: String, defaultValue: T): SavedStateFlow<T>

    /**
     * Returns a [Flow] with the provided [key] from the [SavedStateHandle.getLiveData]
     */
    @MainThread
    fun <T> getFlow(key: String): Flow<T>

}

fun SavedStateHandle.toSavedStateFlowHandle(): SavedStateFlowHandle =
    SavedStateFlowHandleImpl(this)

internal class SavedStateFlowHandleImpl(private val savedStateHandle: SavedStateHandle) : SavedStateFlowHandle {
    override fun <T> get(key: String): T? = savedStateHandle.get(key)

    override fun <T> set(key: String, value: T) {
        savedStateHandle.set(key, value)
    }

    override fun <T> getSavedStateFlow(
        viewModelScope: CoroutineScope,
        key: String,
        defaultValue: T
    ): SavedStateFlow<T> = SavedStateFlowImpl(
        viewModelScope = viewModelScope,
        savedStateHandle = this,
        key = key,
        defaultValue = defaultValue
    )

    override fun <T> getFlow(key: String): Flow<T> = savedStateHandle.getLiveData<T>(key).asFlow()
}