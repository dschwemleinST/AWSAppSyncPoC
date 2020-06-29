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
            { exception ->
                Timber.v(exception, "XXX Query failed ${exception.message}")
                subscriber.onError(exception)
            }
        )
    }

    override fun observeJobChanges() =
        Observable.create<DataStoreItemChange<Job>> { subscriber -> // Never completes
            Amplify.DataStore.observe(
                Job::class.java,
                { Timber.v("XXX Observation began") },
                { itemChange ->
                    Timber.v("XXX Observed Job: $itemChange")
                    subscriber.onNext(itemChange)
                },
                { exception ->
                    Timber.e(exception, "XXX Observation failed ${exception.message}")
                    subscriber.onError(exception)
                },
                { Timber.v("XXX Observation complete") }
            )
        }

    override fun saveJob(job: Job): Single<Job> {
        Timber.v("XXX saveJob $job")
        return Single.create { emitter ->
            Amplify.DataStore.save(
                job,
                { itemChange ->
                    Timber.v("XXX Saved job ${itemChange.item()}")
                    emitter.onSuccess(itemChange.item())
                },
                { exception ->
                    Timber.e(exception, "XXX Save job failed: ${exception.message}")
                    emitter.onError(exception)
                })
        }
    }

    override fun getJob(jobID: String): Single<Job> {
        Timber.v("XXX getJob $jobID")
        return Single.create { emitter ->
            Amplify.DataStore.query(Job::class.java, Where.matches(Job.ID.eq(jobID)),
                { jobs ->
                    val existingJob = jobs.next()
                    Timber.v("XXX Found existing job $existingJob")
                    emitter.onSuccess(existingJob)
                },
                { exception ->
                    Timber.e(exception, "XXX Get job failed: ${exception.message}")
                    emitter.onError(exception)
                })
        }
    }

    private fun createNewJob(
        owner: String,
        status: JobStatus
    ): Job =
        Job.builder() // Without an ID, this will create a new record.
            .title("Job 1")
            .owner(owner)
            .phoneNumber("555-555-1234")
            .address("Some Address")
            .status(status)
            .build()

    override fun deleteJob(job: Job): Completable {
        Timber.v("XXX Deleting Job")
        return Completable.create { emitter ->
            kotlin.runCatching {
                Amplify.DataStore.delete(job, { itemChange ->
                    Timber.v("XXX Success deleting job ${itemChange.item()}")
                    emitter.onComplete()
                }, { exception ->
                    Timber.v(exception, "XXX Error deleting job ${exception.message}")
                    emitter.onError(exception)
                })
            }.onFailure { emitter.onError(it) }
        }
    }
}
