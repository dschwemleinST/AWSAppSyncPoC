package com.servicetitan.awsappsyncpoc.viewmodel

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.servicetitan.awsappsyncpoc.event.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import java.util.Locale

abstract class BaseViewModel : ViewModel() {
    val disposables = CompositeDisposable()

    private val navEvent: SingleLiveEvent<NavDirections> by lazy { return@lazy SingleLiveEvent<NavDirections>() }
    protected val hideKeyboard by lazy { SingleLiveEvent<Any>() }

    fun getNavEvent(): LiveData<NavDirections> {
        return navEvent
    }

    fun getHideKeyboardEvent(): LiveData<Any> = hideKeyboard

    @MainThread
    protected fun navigate(directions: NavDirections) {
        navEvent.value = directions
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()

        disposables.dispose()
    }
}
