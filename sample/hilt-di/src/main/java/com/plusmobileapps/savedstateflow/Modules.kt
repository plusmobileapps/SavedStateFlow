package com.plusmobileapps.savedstateflow

import androidx.lifecycle.SavedStateHandle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@InstallIn(ViewModelComponent::class)
@Module
object SavedStateFlowHandleModule {

    @Provides
    @ViewModelScoped
    fun providesSavedStateFlowHandle(savedStateHandle: SavedStateHandle): SavedStateFlowHandle =
        savedStateHandle.toSavedStateFlowHandle()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsModule {

    @Binds
    abstract fun bindNewsDataSource(
        newsRepository: NewsRepository
    ): NewsDataSource

}