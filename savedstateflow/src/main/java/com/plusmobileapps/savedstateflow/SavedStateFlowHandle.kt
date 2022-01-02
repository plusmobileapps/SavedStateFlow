package com.plusmobileapps.savedstateflow

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper around [androidx.lifecycle.SavedStateHandle] with support for flows
 */
interface SavedStateFlowHandle {

    @MainThread
    operator fun <T> get(key: String): T?

    @MainThread
    operator fun <T> set(key: String, value: T)

    @MainThread
    fun <T> getSavedStateFlow(viewModelScope: CoroutineScope, key: String, defaultValue: T): SavedStateFlow<T>

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
        scope = viewModelScope,
        savedStateHandle = SavedStateHandleWrapperImpl(savedStateHandle),
        key = key,
        defaultValue = defaultValue
    )
}