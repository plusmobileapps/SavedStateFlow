# Hilt Dependency Injection

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is a dependency injection framework built on top of [Dagger](https://dagger.dev/) that helps reduce a lot of boiler plate when injecting dependencies. Hilt can also help reduce the boiler plate when using `SavedStateFlow` by scoping an instance of `SavedStateFlowHandle` to any `ViewModel` that requests it. 

## Hilt ViewModel  

The `saved-state-flow-hilt` artifact provides an instance of `SavedStateFlowHandle` to the `ViewModelComponent` out of the box, so it can be declared in any `ViewModel` constructor like so. 

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

The full version of this `ViewModel` can be found [here](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/hilt-di/src/main/java/com/plusmobileapps/savedstateflowhilt/MainViewModel.kt).   

### Grabbing a Reference to @HiltViewModel

Finally grab a reference to the `ViewModel` using the `by viewmodels { }` delegation function. 

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

}
```

## Assisted Injection 

When using Hilt, it's possible that the `@HiltViewModel` annotation cannot be used when a value needs to be injected into the constructor at runtime. For that, there are a couple of extension methods provided to help inject a `SavedStateFlowHandle` when using Hilt's assisted injection from a `FragmentActivity` or `Fragment`. 

### Assisted Injected ViewModel

```kotlin
class MyAssistedViewModel @AssistedInject constructor(
    @Assisted savedStateFlowHandle: SavedStateFlowHandle,
    @Assisted id: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(savedStateFlowHandle: SavedStateFlowHandle, id: String): MyAssistedViewModel
    }
}
```

### Grab a reference to an Assisted ViewModel

Then in a `Fragment` or a `FragmentActivity`, the `by assistedViewModel` method may be used to get a reference to a assisted injected `ViewModel` as this method provides you an instance of a `SavedStateFlowHandle`. There is also a method for fragments to get a `ViewModel` scoped to its `FragmentActivity` if using the `by assistedActivityViewModel {}` method. 

```kotlin
@AndroidEntryPoint
class AssistedFragment : Fragment() {
    @Inject
    lateinit var factory: MyAssistedViewModel.Factory

    private val viewModel: MyAssistedViewModel by assistedViewModel { savedStateFlowHandle ->
        factory.create(savedStateFlowHandle, arguments?.getString("some-argument-key")!!)
    }
}
```

## Resources 

* [Hilt sample source code](https://github.com/plusmobileapps/SavedStateFlow/tree/main/sample/hilt-di)