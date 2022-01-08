# Manual Dependency Injection 

When creating a simple app making use of the [`AbstractSavedStateViewModelFactory`](https://developer.android.com/reference/androidx/lifecycle/AbstractSavedStateViewModelFactory) to manually inject dependencies, this section showcases a sample using that. 

## ViewModel 

A paired down version of the `ViewModel` for this sample is shown below: 

```kotlin
class MainViewModel(
    savedStateFlowHandle: SavedStateFlowHandle,
    private val newsDataSource: NewsDataSource
) : ViewModel() {

    private val query: SavedStateFlow<String> =
        savedStateFlowHandle.getSavedStateFlow(viewModelScope, "main-viewmodel-query-key", "")

    init {
        observeQuery()
    }

    fun updateQuery(query: String) {
        this.query.value = query
    }

    private fun observeQuery() {
        viewModelScope.launch {
            query.asStateFlow()
                .flatMapLatest { query ->
                    newsDataSource.fetchQuery(query)
                }
                .collect { results ->
                    //TODO update state 
                }
        }
    }

}
```

The full version of this `ViewModel` can be found [here](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/manual-di/src/main/java/com/plusmobileapps/savedstateflowmanual/MainViewModel.kt). 

## ViewModel Factory 

Now to actually create an instance of the `ViewModel` and provide it an instance of `SavedStateFlowHandle`, create a factory class that extends `AbstractSavedStateViewModelFactory` and make use of the extension function `SavedStateHandle.toSavedStateFlowHandle()`. 

```kotlin
class MainViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MainViewModel(handle.toSavedStateFlowHandle(), NewsRepository) as T
    }
}
```

## Grabbing a Reference to ViewModel

Finally an instance of a `ViewModel` can be grabbed from the view using the `by viewmodels { }` delegation function and passing it an instance of the factory. 

```kotlin
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }
}
```

## Resources 

* [Manual DI sample source code](https://github.com/plusmobileapps/SavedStateFlow/tree/main/sample/manual-di)

!!! suggestion
    Manual dependency injection is great for small apps and for understanding the basic concept, however it can lead to writing a lot of boiler plate code creating `ViewModel` factory classes. To help reduce the amount of boiler plate, you can move onto the next section to see how a dependency injection framework like Hilt can be used with `SavedStateFlow`. 