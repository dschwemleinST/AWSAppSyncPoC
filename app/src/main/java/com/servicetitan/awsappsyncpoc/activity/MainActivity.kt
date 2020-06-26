package com.servicetitan.awsappsyncpoc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.servicetitan.awsappsyncpoc.R
import com.servicetitan.awsappsyncpoc.di.provideComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        provideComponent()?.inject(this)
    }
}
