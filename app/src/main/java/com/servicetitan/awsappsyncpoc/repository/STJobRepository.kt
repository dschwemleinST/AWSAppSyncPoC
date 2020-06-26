package com.servicetitan.awsappsyncpoc.repository

import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class STJobRepository @Inject constructor() : JobRepository {

    override fun queryCurrentJobs() = Observable.create<Job> { subscriber ->
        Amplify.DataStore.query(
            Job::class.java,
            { queriedJobs ->
                if (queriedJobs.hasNext()) {
                    Timber.v("XXX Query Found Jobs")
                } else {
                    Timber.v("XXX Query Found No Jobs")
                }

                while (queriedJobs.hasNext()) {
                    subscriber.onNext(queriedJobs.next().apply {
                        Timber.v("XXX Query Found $this")
                        subscriber.onNext(this)
                    })
                }

                Timber.v("XXX Query onComplete")
                subscriber.onComplete()
            },
            {
                Timber.v(it, "XXX Query failed")
                subscriber.onError(it)
            }
        )
    }

    override fun observeJobChanges() = Observable.create<DataStoreItemChange<Job>> { subscriber -> // Never complete
        Amplify.DataStore.observe(
            Job::class.java,
            { Timber.v("XXX Observation began") },
            { itemChange ->
                Timber.v("XXX Observed Job: $itemChange")
                subscriber.onNext(itemChange)
            },
            {
                Timber.e(it, "XXX Observation failed")
                subscriber.onError(it)
            },
            { Timber.v("XXX Observation complete") }
        )
    }

    override fun createOrUpdateJob(
        optionalJobId: String?,
        status: JobStatus,
        owner: String
    ): Single<Job> {
        return optionalJobId?.let { jobId ->
            updateExistingJob(jobId, status, owner)
        } ?: saveJob(
            Job.builder() // Without an ID, this will create a new record.
                .title("Job 1")
                .owner(owner)
                .phoneNumber("555-555-1234")
                .address("Some Address")
                .status(status)
                .build()
        )
    }

    private fun saveJob(job: Job): Single<Job> {
        Timber.v("XXX saveJob $job")
        return Single.create { emitter ->
            Amplify.DataStore.save(
                job,
                {
                    Timber.v("XXX Saved job ${it.item()}")
                    emitter.onSuccess(it.item())
                },
                {
                    Timber.e(it, "XXX Save job failed: ${it.message}")
                    emitter.onError(it)
                })
        }
    }

    private fun updateExistingJob(
        jobId: String,
        status: JobStatus,
        owner: String
    ): Single<Job> {
        Timber.v("XXX Updating Existing Job")
        return Single.create { emitter ->
            Amplify.DataStore.query(Job::class.java, Where.matches(Job.ID.eq(jobId)),
                {
                    val existingJob = it.next()
                    Timber.v("XXX Found existing job $existingJob")

                    // Update the job.
                    existingJob.copyOfBuilder()
                        .status(status)
                        .owner(owner)
                        .build().apply {
                            Timber.v("XXX Saving updated job $this")
                            saveJob(this).subscribe({emitter.onSuccess(it)}, {})
                        }

                    //saveSucceededEvent.call() // TODO: Move to UI thread
                },
                {
                    Timber.e(it, "XXX Find existing failed: ${it.message} for Job ID $jobId")
                    //saveFailedEvent.value = it.message // TODO: Move to UI thread
                })
        }
    }

    override fun deleteJob(job: Job): Completable {
        Timber.v("XXX Deleting Job")
        return Completable.create { emitter ->
            kotlin.runCatching {
                Amplify.DataStore.delete(job, {
                    Timber.v("XXX Success deleting job $it")
                    emitter.onComplete()
                }, {
                    Timber.v("XXX Error deleting job $it")
                    emitter.onError(it)
                })
            }.onFailure { emitter.onError(it) }
        }
    }
}
