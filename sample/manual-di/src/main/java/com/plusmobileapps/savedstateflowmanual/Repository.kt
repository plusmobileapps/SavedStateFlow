package com.plusmobileapps.savedstateflowmanual

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NewsDataSource {
    fun fetchQuery(query: String): Flow<List<String>>
}

object NewsRepository : NewsDataSource {
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