package com.servicetitan.awsappsyncpoc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.servicetitan.awsappsyncpoc.databinding.FragmentMainBinding
import com.servicetitan.awsappsyncpoc.di.provideComponent
import com.servicetitan.awsappsyncpoc.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun observeViewModel() {
        viewModel.init()
    }

    override fun inject() {
        provideComponent()?.inject(this)
    }

    override fun viewModelClass() = MainViewModel::class.java
}