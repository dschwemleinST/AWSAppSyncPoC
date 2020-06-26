package com.servicetitan.awsappsyncpoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
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
        Timber.v("XXX Starting Query")
        jobRepository.queryCurrentJobs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { job -> jobs.value = jobs.value!!.toMutableList().apply {
                    add(job)
                }.sortedBy { it.owner + "   " + it.id } },
                { Timber.v(it, "XXX Error in Query") })
            .addTo(disposables)

        // Observe for changes.
        jobRepository.observeJobChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                { Timber.v(it, "XXX Error in Observe") })
            .addTo(disposables)

        Timber.v("XXX Done with init")
    }

    fun createOrUpdateJob(optionalJobId: String?, status: JobStatus, owner: String) {
        jobRepository.createOrUpdateJob(optionalJobId, status, owner)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Timber.v("XXX Success saving") }, { Timber.v(it, "XXX Error saving") })
            .addTo(disposables)

        hideKeyboard.call()
    }

    fun addNewJob() {
        jobRepository.createOrUpdateJob(null, JobStatus.SCHEDULED, "Michael Keaton")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.v("XXX Success creating new") },
                { Timber.v(it, "XXX Error creating new") })
            .addTo(disposables)
    }

    fun deleteJob(job: Job) {
        jobRepository.deleteJob(job)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.v("XXX Success deleting job") },
                { Timber.v(it, "XXX Error deleting job") })
            .addTo(disposables)
    }
}
