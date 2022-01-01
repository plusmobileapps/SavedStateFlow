package com.plusmobileapps.savedstateflow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    lateinit var viewModel: MainViewModel

    private val savedStateHandle: SavedStateHandle = mockk()
    private val newsDataSource: NewsDataSource = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MainViewModel(savedStateHandle, newsDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `saved state flow returns cache value and fetches results`() = runTest {
        val newQuery = "some new query"
        val expected = listOf<String>("some value", "some second value")
        every { savedStateHandle.get<String>(MainViewModel.SAVED_STATE_QUERY_KEY) } returns null
        every { savedStateHandle.getLiveData<String>(MainViewModel.SAVED_STATE_QUERY_KEY) } returns MutableLiveData("")
        every { newsDataSource.fetchQuery(newQuery) } returns flow {
            emit(expected)
        }

        viewModel.updateQuery(newQuery)

        assertEquals(expected, viewModel.state.value.results)
    }
}