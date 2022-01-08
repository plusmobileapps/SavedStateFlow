package com.plusmobileapps.savedstateflow

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsModule {

    @Binds
    abstract fun bindNewsDataSource(
        newsRepository: NewsRepository
    ): NewsDataSource

}