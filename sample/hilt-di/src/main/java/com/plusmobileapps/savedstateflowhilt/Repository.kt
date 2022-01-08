package com.plusmobileapps.savedstateflowhilt

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface NewsDataSource {
    fun fetchQuery(query: String): Flow<List<String>>
}

@Singleton
class NewsRepository @Inject constructor() : NewsDataSource {
    override fun fetchQuery(query: String): Flow<List<String>> = flow {
        delay(2000L)
        if (query.isEmpty()) {
            emit(emptyList())
            return@flow
        }
        emit(
            listOf(
                "Query: $query \nResult: result1",
                "Query: $query \nResult: result2",
                "Query: $query \nResult: result3"
            )
        )
    }
}