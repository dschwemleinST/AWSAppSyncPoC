package com.servicetitan.awsappsyncpoc.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.servicetitan.awsappsyncpoc.MainApplication

fun Activity.provideComponent() =
    (application as? MainApplication)?.component

fun Fragment.provideComponent() =
    (activity?.application as? MainApplication)?.component
   