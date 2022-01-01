package com.plusmobileapps.savedstateflow

import app.cash.turbine.test
import com.plusmobileapps.savedstateflow.MainViewModel.Companion.SAVED_STATE_QUERY_KEY
import com.plusmobileapps.savedstateflow.MainViewModel.State
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    var coroutinesTestRule = TestCoroutinesRule()

    private lateinit var viewModel: MainViewModel

    private val savedStateHandle: SavedStateFlowHandle = mockk()
    private val newsDataSource: NewsDataSource = mockk()

    private val results = listOf<String>("some value", "some second value")

    private fun setUp(savedStateFlow: SavedStateFlow<String>) {
        every { newsDataSource.fetchQuery("") } returns flow {  }
        every { savedStateHandle.getSavedStateFlow(any(), SAVED_STATE_QUERY_KEY, "") } returns savedStateFlow

        viewModel = MainViewModel(savedStateHandle, newsDataSource)
    }

    @Test
    fun `initial query value exists, should start in loading state and fetch results`() = runBlocking {
        val cachedQuery = "some cached query"
        val savedStateFlow = TestSavedStateFlow<String>("", cachedQuery)
        every { newsDataSource.fetchQuery(cachedQuery) } returns flowOf(results)

        setUp(savedStateFlow)

        viewModel.state.test {
            assertEquals(State(true, cachedQuery, emptyList()), awaitItem())
            assertEquals(State(false, cachedQuery, results), awaitItem())
        }
    }

    @Test
    fun `update query should trigger a new fetch to the repository for results and update state`() = runBlocking {
        val savedStateFlow = TestSavedStateFlow<String>("")
        val newQuery = "some new query"
        every { newsDataSource.fetchQuery(newQuery) } returns flowOf(results)

        setUp(savedStateFlow)

        viewModel.state.test {
            viewModel.updateQuery(newQuery)
            assertEquals(State(false, "", emptyList()), awaitItem())
            assertEquals(State(true, newQuery, emptyList()), awaitItem())
            assertEquals(State(false, newQuery, results), awaitItem())
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