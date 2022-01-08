package com.plusmobileapps.savedstateflowhilt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutinesRule : TestRule {

    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    override fun apply(base: Statement?, description: Description?): Statement = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(mainThreadSurrogate)

            base?.evaluate()

            Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
            mainThreadSurrogate.close()
        }
    }
}