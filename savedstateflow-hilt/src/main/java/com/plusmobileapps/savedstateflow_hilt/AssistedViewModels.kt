@file:Suppress("UNCHECKED_CAST")

package com.plusmobileapps.savedstateflow_hilt

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.plusmobileapps.savedstateflow.SavedStateFlowHandle
import com.plusmobileapps.savedstateflow.toSavedStateFlowHandle

// https://github.com/google/dagger/issues/2287#issuecomment-762249856

/**
 * Get a reference to a [ViewModel] that is assisted injected with a [SavedStateFlowHandle] that is scoped
 * to the [Fragment]
 */
inline fun <reified T : ViewModel> Fragment.assistedViewModel(
    crossinline viewModelProducer: (SavedStateFlowHandle) -> T
): Lazy<T> = viewModels {
    object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ) = viewModelProducer(handle.toSavedStateFlowHandle()) as T
    }
}

/**
 * Get a reference to a [ViewModel] that is assisted injected with a [SavedStateFlowHandle] that is scoped
 * to its Activity
 */
inline fun <reified T : ViewModel> Fragment.assistedActivityViewModel(
    crossinline viewModelProducer: (SavedStateFlowHandle) -> T
): Lazy<T> = activityViewModels {
    object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ) = viewModelProducer(handle.toSavedStateFlowHandle()) as T
    }
}

/**
 * Get a reference to a [ViewModel] that is assisted injected with a [SavedStateFlowHandle] that is scoped
 * to a [FragmentActivity]
 */
inline fun <reified T : ViewModel> FragmentActivity.assistedViewModel(
    crossinline viewModelProducer: (SavedStateFlowHandle) -> T
): Lazy<T> = viewModels {
    object : AbstractSavedStateViewModelFactory(this, intent.extras) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ) = viewModelProducer(handle.toSavedStateFlowHandle()) as T
    }
}

