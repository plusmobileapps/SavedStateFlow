package com.plusmobileapps.savedstateflow.assisted

import androidx.lifecycle.ViewModel
import com.plusmobileapps.savedstateflow.SavedStateFlowHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MyAssistedViewModel @AssistedInject constructor(
    @Assisted savedStateFlowHandle: SavedStateFlowHandle,
    @Assisted id: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(savedStateFlowHandle: SavedStateFlowHandle, id: String): MyAssistedViewModel
    }
}