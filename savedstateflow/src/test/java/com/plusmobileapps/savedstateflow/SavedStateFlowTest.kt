package com.plusmobileapps.savedstateflow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SavedStateFlowTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val savedStateKey = "some key"
    private val initialDefaultValue = "The initial default value when no value in saved state"

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `no saved value should default to initial value`() {
        val savedStateHandle: SavedStateHandle = mockk {
            every { get<String>(savedStateKey) } returns null
            every { getLiveData<String>(savedStateKey) } returns MutableLiveData()
        }

        val testMe = SavedStateFlowImpl<String>(
            TestScope(),
            savedStateHandle,
            savedStateKey,
            initialDefaultValue
        )

        assertEquals(initialDefaultValue, testMe.value)
    }

    @Test
    fun `saved value exists and should be returned`() {
        val savedValue = "some saved value"
        val savedLiveData = MutableLiveData<String>()
        val savedStateHandle: SavedStateHandle = mockk {
            every { get<String>(savedStateKey) } returns savedValue
            every { getLiveData<String>(savedStateKey) } returns savedLiveData
        }

        val testMe =
            SavedStateFlowImpl(TestScope(), savedStateHandle, savedStateKey, initialDefaultValue)

        assertEquals(savedValue, testMe.value)
    }

    @Test
    fun `set value delegates to saved state handle`() {
        val expected = "some new value"
        val savedStateHandle: SavedStateHandle = mockk(relaxUnitFun = true) {
            every { get<String>(savedStateKey) } returns null
            every { getLiveData<String>(savedStateKey) } returns MutableLiveData()
        }
        val testMe = SavedStateFlowImpl<String>(
            TestScope(),
            savedStateHandle,
            savedStateKey,
            initialDefaultValue
        )

        testMe.value = expected

        verify { savedStateHandle.set(savedStateKey, expected) }
    }

}
