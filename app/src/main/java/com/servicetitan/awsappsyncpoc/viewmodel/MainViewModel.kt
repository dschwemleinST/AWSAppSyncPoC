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

    private val main = Dispatchers.Main
    private val io = Dispatchers.IO

    private val mainScope = CoroutineScope(SupervisorJob() + main)
    private val ioScope = CoroutineScope(SupervisorJob() + io)

    @ExperimentalCoroutinesApi
    fun init() {
        // One-time query.
        mainScope.launch {
            jobRepository.queryCurrentJobs()
                .flowOn(io)
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in queryCurrentJobs ${it.message}") }
                .collect { job ->
                    refreshJobInModel(job)
                }
        }

        // Observe for changes.
        mainScope.launch {
            jobRepository.observeJobChanges()
                .flowOn(io)
                .catch { Timber.e(it, "XXX Error in observeJobChanges ${it.message}") }
                .collect { itemChange ->
                    processJobChange(itemChange)
                }
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun updateJobStatus(jobID: String, status: JobStatus) {
        mainScope.launch {
            jobRepository.getJob(jobID)
                .flatMapConcat { job ->
                    jobRepository.saveJob(job.copyOfBuilder().status(status).build())
                }
                .flowOn(io)
                .onCompletion { hideKeyboard.call() }
                .flowOn(main)
                .catch { Timber.e(it, "XXX Error in updateJobStatus ${it.message}") }
                .collect { savedJob ->
                    refreshJobInModel(savedJob)
                }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun updateJobOwner(jobID: String, owner: String) {
        mainScope.launch {
            jobRepository.getJob(jobID)
                .flatMapConcat { job ->
                    jobRepository.saveJob(job.copyOfBuilder().owner(owner).build())
                }
                .flowOn(io)
                .onCompletion { hideKeyboard.call() }
                .flowOn(main)
                .catch { Timber.e(it, "XXX Error in updateJobOwner ${it.message}") }
                .collect { savedJob ->
                    refreshJobInModel(savedJob)
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun addNewJob() {
        mainScope.launch {
            jobRepository.saveJob(generateNewJob())
                .flowOn(io)
                .onCompletion { }
                .catch { Timber.e(it, "XXX Error in addNewJob ${it.message}") }
                .collect { savedJob -> refreshJobInModel(savedJob) }
        }
    }

    @ExperimentalCoroutinesApi
    fun deleteJob(job: Job) {
        ioScope.launch {
            jobRepository.deleteJob(job)
                .onCompletion { }
                .catch { Timber.v(it, "XXX Error in deleteJob ${it.message}") }
                .collect()
        }
    }

    private fun generateNewJob(): Job {
        return Job.Builder()
            .title("Random title")
            .owner("Michael Keaton")
            .phoneNumber("555-555-1234")
            .address("Some Address")
            .status(JobStatus.SCHEDULED)
            .build()
    }

    private fun refreshJobInModel(job: Job) {
        jobs.value = jobs.value!!.toMutableList().apply {
            removeIf { it.id == job.id }
            add(job)
        }.sortedBy { jobSortString(it) }
    }

    private fun processJobChange(itemChange: DataStoreItemChange<Job>) {
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
            }.sortedBy { jobSortString(it) }
    }

    private fun jobSortString(it: Job) =
        it.owner + "   " + it.id
}

