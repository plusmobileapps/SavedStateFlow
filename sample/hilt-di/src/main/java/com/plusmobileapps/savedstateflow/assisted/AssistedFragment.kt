package com.plusmobileapps.savedstateflow.assisted

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.plusmobileapps.savedstateflow_hilt.assistedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AssistedFragment : Fragment() {
    @Inject
    lateinit var factory: MyAssistedViewModel.Factory

    private val viewModel: MyAssistedViewModel by assistedViewModel { savedStateFlowHandle ->
        factory.create(savedStateFlowHandle, arguments?.getString(ARGUMENTS_KEY)!!)
    }

    companion object {
        const val ARGUMENTS_KEY = "some-argument-key"

        fun newInstance(id: String): AssistedFragment = AssistedFragment().apply {
            arguments = bundleOf(ARGUMENTS_KEY to id)
        }
    }
}