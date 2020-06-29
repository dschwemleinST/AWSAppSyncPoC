package com.servicetitan.awsappsyncpoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import com.servicetitan.awsappsyncpoc.repository.JobRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val jobRepository: JobRepository) :
    BaseViewModel() {
    private val jobs by lazy { MutableLiveData<List<Job>>(emptyList()) }
    fun getJobs(): LiveData<List<Job>> = jobs

    private val main = Dispatchers.Main.immediate
    private val io = Dispatchers.IO

    private val mainScope = CoroutineScope(SupervisorJob() + main)
    private val ioScope = CoroutineScope(SupervisorJob() + io)

    @ExperimentalCoroutinesApi
    fun init() {
        // One-time query.
        ioScope.launch {
            jobRepository.queryCurrentJobs()
                .flowOn(main)
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in queryCurrentJobs ${it.message}") }
                .toList().forEach { job ->
                    refreshJobInModel(job)
                }
        }

        // Observe for changes.
        ioScope.launch {
            jobRepository.observeJobChanges()
                .flowOn(main)
                .onEach { itemChange ->
                    mainScope.launch { // Why needed, given the above flowOn()?
                        jobs.value =
                            jobs.value!!.toMutableList().apply {
                                when (itemChange.type()) {
                                    DataStoreItemChange.Type.CREATE ->
                                        add(itemChange.item())
                                    DataStoreItemChange.Type.UPDATE -> {
                                        removeIf { it.id == itemChange.item().id }
                                        add(itemChange.item())
                                    }
                                    DataStoreItemChange.Type.DELETE ->
                                        remove(itemChange.item())
                                }
                            }.sortedBy { it.owner + "   " + it.id }
                    }
                }
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in observeJobChanges ${it.message}") }
                .collect()
        }
    }

    @ExperimentalCoroutinesApi
    fun updateJobStatus(jobID: String, status: JobStatus) {
        ioScope.launch {
            jobRepository.getJob(jobID)
                .flatMapConcat { job ->
                    jobRepository.saveJob(job.copyOfBuilder().status(status).build())
                }
                .flowOn(main)
                .onEach { job -> refreshJobInModel(job) }
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in updateJobStatus ${it.message}") }
                .single()
        }

        mainScope.launch {
            hideKeyboard.call()
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun updateJobOwner(jobID: String, owner: String) {
        ioScope.launch {
            jobRepository.getJob(jobID)
                .flatMapConcat { job ->
                    jobRepository.saveJob(job.copyOfBuilder().owner(owner).build())
                }
                .flowOn(main)
                .onEach { job -> refreshJobInModel(job) }
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in updateJobStatus ${it.message}") }
                .single()
        }

        mainScope.launch {
            hideKeyboard.call()
        }
    }

    fun addNewJob() {
        ioScope.launch {
            jobRepository.saveJob(
                Job.Builder()
                    .title("Random title")
                    .owner("Michael Keaton")
                    .phoneNumber("555-555-1234")
                    .address("Some Address")
                    .status(JobStatus.SCHEDULED)
                    .build()
            )
                .flowOn(main)
                .onEach { savedJob -> refreshJobInModel(savedJob) }
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in saveJob ${it.message}") }
                .single()
        }
    }

    fun deleteJob(job: Job) {
        ioScope.launch {
            kotlin.runCatching {
                jobRepository.deleteJob(job).single()
            }.onFailure { Timber.v(it, "XXX Error deleting job ${it.message}") }
        }
    }

    private fun refreshJobInModel(job: Job) {
        mainScope.launch {
            jobs.value = jobs.value!!.toMutableList().apply {
                removeIf { it.id == job.id }
                add(job)
            }.sortedBy { it.owner + "   " + it.id }
        }
    }
}

