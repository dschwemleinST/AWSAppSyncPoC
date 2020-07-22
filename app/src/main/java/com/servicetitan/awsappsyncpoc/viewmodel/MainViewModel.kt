package com.servicetitan.awsappsyncpoc.viewmodel

import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import com.servicetitan.awsappsyncpoc.repository.JobRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(private val jobRepository: JobRepository) :
    BaseViewModel() {

    private val jobs = MutableStateFlow<List<Job>>(emptyList())
    fun getJobs(): Flow<List<Job>> = jobs

    fun init() {
        // One-time query.
        jobRepository.queryCurrentJobs()
            .onEach { job -> refreshJobInModel(job) }
            .catch { Timber.e(it, "XXX Error in queryCurrentJobs ${it.message}") }
            .launchIn(viewModelScope)

        // Observe for changes.
        jobRepository.observeJobChanges()
            .onEach { itemChange -> processJobChange(itemChange) }
            .catch { Timber.e(it, "XXX Error in observeJobChanges ${it.message}") }
            .launchIn(viewModelScope)
    }

    fun updateJobStatus(jobID: String, status: JobStatus) {
        jobRepository.getJob(jobID)
            .flatMapConcat { gotJob -> jobRepository.saveJob(gotJob.copyOfBuilder().status(status).build()) }
            .onEach { savedJob -> refreshJobInModel(savedJob) }
            .onCompletion { hideKeyboard.call() }
            .catch { Timber.e(it, "XXX Error in updateJobStatus ${it.message}") }
            .launchIn(viewModelScope)
    }

    fun updateJobOwner(jobID: String, owner: String) {
        jobRepository.getJob(jobID)
            .flatMapConcat { gotJob ->
                jobRepository.saveJob(
                    gotJob.copyOfBuilder().owner(owner).build()
                )
            }
            .onEach { savedJob -> refreshJobInModel(savedJob) }
            .onCompletion { hideKeyboard.call() }
            .catch { Timber.e(it, "XXX Error in updateJobOwner ${it.message}") }
            .launchIn(viewModelScope)
    }

    fun addNewJob() {
        jobRepository.saveJob(generateNewJob())
            .onEach { savedJob -> refreshJobInModel(savedJob) }
            .catch { Timber.e(it, "XXX Error in addNewJob ${it.message}") }
            .launchIn(viewModelScope)
    }

    fun deleteJob(job: Job) {
        jobRepository.getJob(job.id)
            .flatMapConcat { gotJob -> jobRepository.deleteJob(gotJob) }
            .catch { Timber.v(it, "XXX Error in deleteJob ${it.message}") }
            .launchIn(viewModelScope)
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
        jobs.value = jobs.value.toMutableList().apply {
            removeIf { it.id == job.id }
            add(job)
        }.sortedBy { jobSortString(it) }
    }

    private fun processJobChange(itemChange: DataStoreItemChange<Job>) {
        jobs.value =
            jobs.value.toMutableList().apply {
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

