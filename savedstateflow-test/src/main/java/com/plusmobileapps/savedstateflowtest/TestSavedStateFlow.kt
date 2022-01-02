package com.plusmobileapps.savedstateflowtest

import com.plusmobileapps.savedstateflow.SavedStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A test helper for [SavedStateFlow] that allows for quick mocking of the default or
 * cached value returned from a process death.
 *
 * @param defaultValue the default value to be returned when no cached value present
 * @param cachedValue cached value that would simulate being restored from a process death
 */
class TestSavedStateFlow<T : Any>(defaultValue: T, cachedValue: T? = null) : SavedStateFlow<T> {

    private val stateFlow = MutableStateFlow(cachedValue ?: defaultValue)

    override var value: T by stateFlow::value

    override fun asStateFlow(): StateFlow<T> = stateFlow

}