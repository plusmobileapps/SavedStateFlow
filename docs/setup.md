# Setup SavedStateFlow

`SavedStateFlow` is available through Maven Central so declare that in the root `build.gradle`. 

=== "Groovy"

    ``` c
    buildscript {
        repositories {
            mavenCentral()
        }   
    }
    ```

=== "Kotlin"

    ```kotlin
    buildscript {
        repositories {
            mavenCentral()
        }
    }
    ```

For all artifacts listed below, replace `<version>` with the latest version available on Maven Central 
[![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps/saved-state-flow?color=blue)](https://search.maven.org/artifact/com.plusmobileapps/saved-state-flow) or check out [releases](https://github.com/plusmobileapps/SavedStateFlow/releases) for previous versions. For example: 

```
implementation "com.plusmobileapps:saved-state-flow:1.0"
```

## SavedStateFlow 

For the basic `SavedStateFlow` API that could be used in any dependency injection framework, please import the following. 

=== "Groovy"

    ``` c
    implementation "com.plusmobileapps:saved-state-flow:<version>"
    ```

=== "Kotlin"

    ```kotlin
    implementation("com.plusmobileapps:saved-state-flow:<version>")
    ```

## SavedStateFlow - Hilt 

If using [Hilt](https://developer.android.com/training/dependency-injection/hilt-android), this is the only dependency that needs to be imported as it bundles in the `SavedStateFlow` API and allows `SavedStateFlowHandle` to be injected into any `@HiltViewModel`. 

=== "Groovy"

    ``` c
    implementation "com.plusmobileapps:saved-state-flow-hilt:<version>"
    ```

=== "Kotlin"

    ```kotlin
    implementation("com.plusmobileapps:saved-state-flow-hilt:<version>")
    ```

## Testing

For the `TestSavedStateFlow` artifact, import the following for testing. 

=== "Groovy"

    ``` c
    testImplementation "com.plusmobileapps:saved-state-flow-test:<version>"
    ```

=== "Kotlin"

    ```kotlin
    testImplementation("com.plusmobileapps:saved-state-flow-test:<version>")
    ```
