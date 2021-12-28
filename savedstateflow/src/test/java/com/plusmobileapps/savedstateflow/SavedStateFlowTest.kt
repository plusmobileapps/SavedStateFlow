package com.plusmobileapps.savedstateflow

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SavedStateFlowTest {

    private val savedStateKey = "some key"
    private val initialDefaultValue = "The initial default value when no value in saved state"

    @Test
    fun `no saved value should default to initial value`() = runTest {
        val savedStateHandle: SavedStateHandleWrapper<String> = mockk {
            every { getValue(savedStateKey) } returns null
            every { getFlow(savedStateKey) } returns flow { }
        }

        val testMe = SavedStateFlowImpl<String>(
            this,
            savedStateHandle,
            savedStateKey,
            initialDefaultValue
        )

        assertEquals(initialDefaultValue, testMe.value)
    }

    @Test
    fun `saved value exists and should be returned`() = runTest {
        val savedValue = "some saved value"
        val savedStateHandle: SavedStateHandleWrapper<String> = mockk {
            every { getValue(savedStateKey) } returns savedValue
            every { getFlow(savedStateKey) } returns flow { }
        }

        val testMe =
            SavedStateFlowImpl(this, savedStateHandle, savedStateKey, initialDefaultValue)

        assertEquals(savedValue, testMe.value)
    }

    @Test
    fun `set value delegates to saved state handle`() = runTest {
        val expected = "some new value"
        val savedStateHandle: SavedStateHandleWrapper<String> = mockk(relaxUnitFun = true) {
            every { getValue(savedStateKey) } returns null
            every { getFlow(savedStateKey) } returns flow { }
        }
        val testMe = SavedStateFlowImpl<String>(
            this,
            savedStateHandle,
            savedStateKey,
            initialDefaultValue
        )

        testMe.value = expected

        verify { savedStateHandle.setValue(savedStateKey, expected) }
    }

    @Test
    fun `state flow gets new values from saved state handle`() = runTest {
        val expected = listOf("first value", "second value", "third value")
        val savedStateHandle: SavedStateHandleWrapper<String> = mockk(relaxUnitFun = true) {
            every { getValue(savedStateKey) } returns null
            every { getFlow(savedStateKey) } returns flow {
                expected.forEach { emit(it) }
            }
        }

        SavedStateFlowImpl<String>(
            this,
            savedStateHandle,
            savedStateKey,
            initialDefaultValue
        ).asStateFlow()
            .test {
                assertEquals(initialDefaultValue, awaitItem())
                assertEquals(expected[0], awaitItem())
                assertEquals(expected[1], awaitItem())
                assertEquals(expected[2], awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
    }
}
