package com.servicetitan.awsappsyncpoc.viewmodel

import com.servicetitan.awsappsyncpoc.repository.JobRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(private val jobRepository: JobRepository) :
    BaseViewModel() {

    fun init() {

    }
}

