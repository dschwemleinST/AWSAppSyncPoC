package com.servicetitan.awsappsyncpoc

import com.servicetitan.awsappsyncpoc.dagger.DaggerTestServiceTitanComponent
import com.servicetitan.awsappsyncpoc.dagger.ServiceTitanComponent

class TestMainApplication : MainApplication() {
    override val component: ServiceTitanComponent by lazy {
        DaggerTestServiceTitanComponent.factory().create(this)
    }
}
