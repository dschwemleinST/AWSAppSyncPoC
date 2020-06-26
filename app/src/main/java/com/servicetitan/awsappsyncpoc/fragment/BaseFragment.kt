package com.servicetitan.awsappsyncpoc.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.servicetitan.awsappsyncpoc.ServiceTitanApplication
import com.servicetitan.awsappsyncpoc.di.ApplicationComponent
import javax.inject.Inject
import com.servicetitan.awsappsyncpoc.viewmodel.BaseViewModel

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {
    // When a fragment has been navigated away from, this flag is used to disallow another navigation
    // until the view is destroyed.
    private var didInitiateNavigation = false

    protected var _binding: VB? = null
    protected val binding get() = _binding!!

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    protected val component: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as ServiceTitanApplication).component
    }

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(viewModelClass())
    }

    @CallSuper
    override fun onAttach(context: Context) {
        inject()

        super.onAttach(context)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNavEvent().observe(viewLifecycleOwner, Observer { directions: NavDirections ->
            if (!didInitiateNavigation) {
                findNavController().navigate(directions)
                didInitiateNavigation = true
            }
        })
        viewModel.getHideKeyboardEvent().observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        observeViewModel()
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    abstract fun inject()

    abstract fun viewModelClass(): Class<VM>

    abstract fun observeViewModel()

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        didInitiateNavigation = false
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { focusedView ->
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                focusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}
