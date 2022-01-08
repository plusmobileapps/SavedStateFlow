# Overview

## What is SavedStateFlow? 

`SavedStateFlow` is a Kotlin [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) that can survive the Android process death. 

## Why SavedStateFlow? 

[`SavedStateHandle`](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) is a great tool to persist state across the Android process death, however this only officially supports reactive updates through `LiveData`. There is a nice extension function that exists in the support library that can convert `LiveData` to a `Flow`, however using this directly in a `ViewModel` test requires setting up the test to work with `LiveData` and `LiveData` can technically have a null value. 

This is where `SavedStateFlow` comes in being a simple wrapper around [SavedStateHandle.getLiveData()](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate#savedstatehandle) while enabling non null initial values exposing the state as a `StateFlow`. 

## Basic Usage

Inject a `SavedStateFlowHandle` into a `ViewModel` by using the extension function on `SavedStateHandle`.

```kotlin
val savedStateHandle: SavedStateHandle = TODO()

val savedStateFlowHandle: SavedStateFlowHandle = 
    savedStateHandle.toSavedStateFlowHandle()
```

!!! info 
    Please refer to the samples to see how to get a reference to `SavedStateHandle`.
    
    * [manual injection](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/manual-di/src/main/java/com/plusmobileapps/savedstateflowmanual/MainViewModel.kt#L27) using the `AbstractSavedStateViewModelFactory`
    * The `saved-state-flow-hilt` artifact automatically scopes `SavedStateFlowHandle` to `ViewModel`'s so there is no need to get a reference to a `SavedStateHandle`. 

Once a `SavedStateFlowHandle` is created, inject it in a `ViewModel` and retrieve a `SavedStateFlow`. 

```kotlin
class MainViewModel(
    savedStateFlowHandle: SavedStateFlowHandle,
    private val newsDataSource: NewsDataSource
) : ViewModel() {

    private val query: SavedStateFlow<String> =
        savedStateFlowHandle.getSavedStateFlow(
            viewModelScope = viewModelScope, // scope for updates to be collected
            key = "main-viewmodel-query-key", // unique key for the property
            defaultValue = "" // used when there is no previously saved value upon restoration
        )

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
                    // fetch the results for the latest query
                    newsDataSource.fetchQuery(query)
                }
                .collect { results ->
                    // Update with the latest results
                }
        }
    }
}
```

!!! warning
    Since `SavedStateFlow` is a wrapper around `SavedStateHandle`, the following note from the [documentation](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) should be observed. 

    > State must be simple and lightweight. For complex or large data, you should use [local persistence](https://developer.android.com/topic/libraries/architecture/saving-states#local).