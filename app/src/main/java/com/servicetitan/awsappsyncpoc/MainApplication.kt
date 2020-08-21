package com.servicetitan.awsappsyncpoc

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.servicetitan.awsappsyncpoc.di.DaggerApplicationComponent
import com.servicetitan.awsappsyncpoc.di.ApplicationComponent
import timber.log.Timber

open class MainApplication : Application() {
    open val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}