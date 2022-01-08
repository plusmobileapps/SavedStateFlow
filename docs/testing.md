# Testing 

The primary motivation for creating this library was to help make testing easier when working with `SavedStateHandle.getLiveData()` and converting this value to a `Flow`, then to a `StateFlow`. Without the abstraction `SavedStateFlowHandle` provides, one would have to use `LiveData` when testing and remember to always add the [`InstantTaskExecutorRule`](https://developer.android.com/reference/androidx/arch/core/executor/testing/InstantTaskExecutorRule). If your app simply uses `StateFlow` to manage state, then needing to work with `LiveData` and adding this rule can increase the cognitive load when working in a project. 

## TestSavedStateFlow

Since the underlying [implementation](https://github.com/plusmobileapps/SavedStateFlow/blob/main/savedstateflow/src/main/java/com/plusmobileapps/savedstateflow/SavedStateFlow.kt) of `SavedStateFlow` is delegating to the `SavedStateHandle`, using the actual implementation would require using `LiveData` and `InstantTaskExecutorRule`. So to prevent the need of using either of those in tests, there is a test artifact that can be used called `TestSavedStateFlow` which simply swaps out the implementation with a `MutableStateFlow`. 

So one simple test using setup using `TestSavedStateFlow` might look like the following. The mocking library in the samples is [Mockk](https://mockk.io/). 

```kotlin
class SomeTest {
    @Test
    fun `some test`() = runBlocking {
        val savedStateHandle: SavedStateFlowHandle = mockk()
        val savedStateFlow = TestSavedStateFlow<String>(
            defaultValue = "", 
            cachedValue = "some cached value"
        )
        every { savedStateHandle.getSavedStateFlow(any(), "some-key", "") } returns savedStateFlow

        val viewModel = MyViewModel(savedStateHandle)
        // omitted test code
    }
}
```

### Test Setup 

Both samples for manual DI and Hilt have the same constructor, therefore have the same test structure. So when setting up the test, a [`TestCoroutineTestRule`](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/hilt-di/src/test/java/com/plusmobileapps/savedstateflowhilt/TestCoroutineRule.kt) is needed to override the main dispatcher being used by the `viewModelScope`. Then the following setup will be used to setup each of the following tests one could write using `TestSavedStateFlow`. 

```kotlin
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

}
```

Then to actually test the values emitted by the `Flow`, [Turbine](https://github.com/cashapp/turbine) is a great testing library for verifying values emitted by a `Flow`. 

### Test Default Value 

The `cachedValue` has a default value of null, so that can be omitted in tests not concerned with a cached value when restoring from a process death and just use the `defaultValue`. 

```kotlin 
@Test
fun `default value test`() {
    val savedStateFlow = TestSavedStateFlow<String>("some default value")
    ....
}
```

### Test Cached Value 

```kotlin
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
```

### Test Value Changes

```kotlin
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
```


## Resources 

* [`TestSavedStateFlow` source code](https://github.com/plusmobileapps/SavedStateFlow/blob/main/savedstateflow-test/src/main/java/com/plusmobileapps/savedstateflowtest/TestSavedStateFlow.kt)
* [Hilt sample test folder](https://github.com/plusmobileapps/SavedStateFlow/tree/main/sample/hilt-di/src/test/java/com/plusmobileapps/savedstateflowhilt)
* [Manual DI sample test folder](https://github.com/plusmobileapps/SavedStateFlow/tree/main/sample/manual-di/src/test/java/com/plusmobileapps/savedstateflowmanual)
* [Turbine](https://github.com/cashapp/turbine) - A small testing library for kotlinx.coroutines Flow
* [Mockk](https://mockk.io/) - Kotlin mocking library