package com.servicetitan.awsappsyncpoc

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TestServiceTitanApplicationRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application =
        Instrumentation.newApplication(TestMainApplication::class.java, context)
}
