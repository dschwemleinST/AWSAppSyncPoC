package com.servicetitan.awsappsyncpoc.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceTitanModule::class])
interface TestServiceTitanComponent : ServiceTitanComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestServiceTitanComponent
    }
}