package com.servicetitan.awsappsyncpoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import com.servicetitan.awsappsyncpoc.extension.scheduleIOToMainThread
import com.servicetitan.awsappsyncpoc.repository.JobRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val jobRepository: JobRepository) :
    BaseViewModel() {
    private val jobs by lazy { MutableLiveData<List<Job>>(emptyList()) }
    fun getJobs(): LiveData<List<Job>> = jobs

    fun init() {
        // One-time query.
        jobRepository.queryCurrentJobs()
            .scheduleIOToMainThread()
            .subscribe(
                { job ->
                    refreshJobInModel(job)
                },
                { Timber.v(it, "XXX Error in Query ${it.message}") })
            .addTo(disposables)

        // Observe for changes.
        jobRepository.observeJobChanges()
            .scheduleIOToMainThread()
            .subscribe(
                { itemChange ->
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
                },
                { Timber.v(it, "XXX Error in Observe ${it.message}") })
            .addTo(disposables)
    }

    fun updateJobStatus(jobID: String, status: JobStatus) {
        jobRepository.getJob(jobID)
            .flatMap { job ->
                jobRepository.saveJob(job.copyOfBuilder().status(status).build())
            }
            .scheduleIOToMainThread()
            .subscribe(
                { savedJob ->
                    refreshJobInModel(savedJob)
                }, { Timber.v(it, "XXX Error in updateJobStatus: ${it.message}") })
            .addTo(disposables)

        hideKeyboard.call()
    }

    fun updateJobOwner(jobID: String, owner: String) {
        jobRepository.getJob(jobID)
            .flatMap { job ->
                jobRepository.saveJob(job.copyOfBuilder().owner(owner).build())
            }
            .scheduleIOToMainThread()
            .subscribe(
                { savedJob ->
                    refreshJobInModel(savedJob)
                }, { Timber.v(it, "XXX Error in updateJobOwner: ${it.message}") })
            .addTo(disposables)

        hideKeyboard.call()
    }

    fun addNewJob() {
        jobRepository.saveJob(
            Job.Builder()
                .title("Random title")
                .owner("Michael Keaton")
                .phoneNumber("555-555-1234")
                .address("Some Address")
                .status(JobStatus.SCHEDULED)
                .build()
        )
            .scheduleIOToMainThread()
            .subscribe(
                { savedJob ->
                    refreshJobInModel(savedJob)
                },
                { Timber.v(it, "XXX Error creating new ${it.message}") })
            .addTo(disposables)
    }

    fun deleteJob(job: Job) {
        jobRepository.deleteJob(job)
            .scheduleIOToMainThread()
            .subscribe(
                { },
                { Timber.v(it, "XXX Error deleting job ${it.message}") })
            .addTo(disposables)
    }

    private fun refreshJobInModel(job: Job) {
        jobs.value = jobs.value!!.toMutableList().apply {
            removeIf { it.id == job.id }
            add(job)
        }.sortedBy { it.owner + "   " + it.id }
    }
}

