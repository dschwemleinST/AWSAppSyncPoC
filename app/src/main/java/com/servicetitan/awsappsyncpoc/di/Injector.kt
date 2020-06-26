package com.servicetitan.awsappsyncpoc.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.servicetitan.awsappsyncpoc.ServiceTitanApplication

fun Activity.provideComponent() =
    (application as? ServiceTitanApplication)?.component

fun Fragment.provideComponent() =
    (activity?.application as? ServiceTitanApplication)?.component
   