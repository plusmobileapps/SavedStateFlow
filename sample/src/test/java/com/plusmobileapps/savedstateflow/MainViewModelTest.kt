package com.plusmobileapps.savedstateflow

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    lateinit var viewModel: MainViewModel

    private val savedStateHandle: SavedStateFlowHandle = mockk()
    private val newsDataSource: NewsDataSource = mockk()
    private val savedStateFlow: SavedStateFlow<String> = TestSavedStateFlow<String>("")

    @Before
    fun setUp() {
        every { newsDataSource.fetchQuery("") } returns flow {  }
        every {
            savedStateHandle.getSavedStateFlow(
                any(),
                MainViewModel.SAVED_STATE_QUERY_KEY,
                ""
            )
        } returns savedStateFlow
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MainViewModel(savedStateHandle, newsDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `saved state flow returns cache value and fetches results`() = runBlocking {
        val newQuery = "some new query"
        val results = listOf<String>("some value", "some second value")
        val expectedState = MainViewModel.State(false, newQuery, results)
        every { newsDataSource.fetchQuery(newQuery) } returns flowOf(results)

        viewModel.state.test {
            viewModel.updateQuery(newQuery)
            assertEquals(MainViewModel.State(false, "", emptyList()), awaitItem())
            assertEquals(MainViewModel.State(true, newQuery, emptyList()), awaitItem())
            assertEquals(expectedState, awaitItem())
        }
    }
}

/**
 * TODO move into its own test module
 */
class TestSavedStateFlow<T : Any>(defaultValue: T, cachedValue: T? = null) : SavedStateFlow<T> {

    private val stateFlow = MutableStateFlow(cachedValue ?: defaultValue)

    override var value: T by stateFlow::value

    override fun asStateFlow(): StateFlow<T> = stateFlow
}