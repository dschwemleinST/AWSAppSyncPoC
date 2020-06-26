package com.servicetitan.awsappsyncpoc

import com.servicetitan.awsappsyncpoc.dagger.ServiceTitanComponent
import com.servicetitan.awsappsyncpoc.dagger.DaggerTestServiceTitanComponent

class TestServiceTitanApplication : ServiceTitanApplication() {
    override val component: ServiceTitanComponent by lazy {
        DaggerTestServiceTitanComponent.factory().create(this)
    }
}
