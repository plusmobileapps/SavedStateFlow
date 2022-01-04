# Hilt Dependency Injection

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is a dependency injection framework built on top of [Dagger](https://dagger.dev/) that helps reduce a lot of boiler plate when injecting dependencies. Hilt can also help reduce the boiler plate when using `SavedStateFlow` by scoping an instance of `SavedStateFlowHandle` to any `ViewModel` that requests it. 

## ViewModel 

A paired down version of the `ViewModel` for this sample is shown below: 

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
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
                    // TODO update state with latest results
                }
        }
    }
}
```

The full version of this `ViewModel` can be found [here](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/hilt-di/src/main/java/com/plusmobileapps/savedstateflow/MainViewModel.kt). 


## Scoping SavedStateFlowHandle to ViewModel's

Hilt provides the ability of scoping dependencies, so to ensure any `ViewModel` can get a reference to a `SavedStateFlow` the [ViewModel scope](https://dagger.dev/hilt/view-model.html) can be used to scope a `SavedStateFlowHandle` to every `ViewModel`. Considering the following from the documentation: 

> SavedStateHandle is a default binding available to all Hilt View Models. Only dependencies from the ViewModelComponent and its parent components can be provided into the ViewModel

The extension function `SavedStateHandle.toSavedStateFlowHandle()` can be used in a module that is installed in the `ViewModelComponent`. 

```kotlin
@InstallIn(ViewModelComponent::class)
@Module
object SavedStateFlowHandleModule {

    @Provides
    @ViewModelScoped
    fun providesSavedStateFlowHandle(savedStateHandle: SavedStateHandle): SavedStateFlowHandle =
        savedStateHandle.toSavedStateFlowHandle()

}
```

## Grabbing a Reference to ViewModel

Finally grab a reference to the `ViewModel` using the `by viewmodels { }` delegation function. 

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

}
```

## Resources 

* [Hilt sample source code](https://github.com/plusmobileapps/SavedStateFlow/tree/main/sample/hilt-di)