package com.servicetitan.awsappsyncpoc.di

import android.content.Context
import com.servicetitan.awsappsyncpoc.activity.MainActivity
import com.servicetitan.awsappsyncpoc.fragment.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class, ViewModelModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
}