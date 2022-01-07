package com.plusmobileapps.savedstateflow_hilt

import androidx.lifecycle.SavedStateHandle
import com.plusmobileapps.savedstateflow.SavedStateFlowHandle
import com.plusmobileapps.savedstateflow.toSavedStateFlowHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
object SavedStateFlowHandleModule {

    @Provides
    @ViewModelScoped
    fun providesSavedStateFlowHandle(savedStateHandle: SavedStateHandle): SavedStateFlowHandle =
        savedStateHandle.toSavedStateFlowHandle()

}