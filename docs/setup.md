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

Then in the `app/build.gradle`, import the dependencies replacing `<version>` with the latest version available. [![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps/saved-state-flow?color=blue)](https://search.maven.org/artifact/com.plusmobileapps/saved-state-flow)

=== "Groovy"

    ``` c
    implementation "com.plusmobileapps:saved-state-flow:<version>"
    testImplementation "com.plusmobileapps:saved-state-flow-test:<version>"
    ```

=== "Kotlin"

    ```kotlin
    implementation("com.plusmobileapps:saved-state-flow:<version>")
    testImplementation("com.plusmobileapps:saved-state-flow-test:<version>")
    ```