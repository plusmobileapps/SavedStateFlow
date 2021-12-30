# SavedStateFlow
A Kotlin StateFlow that persists the Android process death

* [SavedStateFlow.kt](https://github.com/plusmobileapps/SavedStateFlow/blob/main/savedstateflow/src/main/java/com/plusmobileapps/savedstateflow/SavedStateFlow.kt)
* [ViewModel example](https://github.com/plusmobileapps/SavedStateFlow/blob/main/sample/src/main/java/com/plusmobileapps/savedstateflow/MainViewModel.kt)

![](docs/saved-state-flow.gif)

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