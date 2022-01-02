# SavedStateFlow

`SavedStateFlow` is a Kotlin [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) wrapper around [SavedStateHandle.getLiveData()](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate)

* [SavedStateFlow.kt](https://github.com/plusmobileapps/SavedStateFlow/blob/main/savedstateflow/src/main/java/com/plusmobileapps/savedstateflow/SavedStateFlow.kt) - all the code for this simple library
* [ViewModel example](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/src/main/java/com/plusmobileapps/savedstateflow/MainViewModel.kt) - sample usage of `SavedStateFlow` in a `ViewModel`

![](docs/saved-state-flow.gif)

## Setup 

[![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps/saved-state-flow?color=blue)](https://search.maven.org/artifact/com.plumobileapps/saved-state-flow)

### Groovy Gradle

```groovy
implementation "com.plusmobileapps:saved-state-flow:<version>"
testImplementation "com.plusmobileapps:saved-state-flow-test:<version>"
```

### Kotlin Gradle

```kotlin
implementation("com.plusmobileapps:saved-state-flow:<version>")
testImplementation("com.plusmobileapps:saved-state-flow-test:<version>")
```

## Usage

Inject a `SavedStateHandle` into a `ViewModel`, now use the `SavedStateFlow` extension function. 

```kotlin
class MainViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val query = SavedStateFlow(
        savedStateHandle = savedStateHandle,
        key = "main-viewmodel-query-key",
        defaultValue = ""
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
                    NewsRepository.fetchQuery(query) // fetch latest query results
                }
                .collect { results ->
                    // TODO post latest results
                }
        }
    }
    
}
```

## How To Publish Locally 

Add the following properties in your `local.properties` file.

```
signing.keyId=gpg-key-id
signing.password=gpg-key-passphrase
signing.key=gpg-key
ossrhUsername=jira-username
ossrhPassword=jira-password
sonatypeStagingProfileId=some-profile-id
```

## Resources

* [How to publish libraries in 2021](https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/)