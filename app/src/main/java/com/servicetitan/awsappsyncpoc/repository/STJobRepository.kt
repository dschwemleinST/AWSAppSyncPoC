package com.servicetitan.awsappsyncpoc.repository

import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class STJobRepository @Inject constructor() : JobRepository {

    @ExperimentalCoroutinesApi
    override fun queryCurrentJobs() = callbackFlow<Job> {
        Amplify.DataStore.query(
            Job::class.java,
            { queriedJobs ->
                if (queriedJobs.hasNext()) {
                    Timber.v("XXX Query Found Jobs")
                } else {
                    Timber.v("XXX Query Found No Jobs")
                }

                queriedJobs.forEach { sendBlocking(it) }

                Timber.v("XXX Query onComplete")
            },
            { exception ->
                Timber.v(exception, "XXX Query failed ${exception.message}")
                throw exception
            }
        )
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override fun observeJobChanges() = callbackFlow<DataStoreItemChange<Job>> {
        Amplify.DataStore.observe(
            Job::class.java,
            { Timber.v("XXX Observation began") },
            { itemChange ->
                Timber.v("XXX Observed Job: $itemChange")
                sendBlocking(itemChange)
            },
            { exception ->
                Timber.e(exception, "XXX Observation failed ${exception.message}")
                cancel(CancellationException("API Error", exception))
                throw exception
            },
            {
                Timber.v("XXX Observation complete")
            }
        )
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override fun saveJob(job: Job) = callbackFlow<Job>{
        Timber.v("XXX saveJob $job")
        Amplify.DataStore.save(
            job,
            { itemChange ->
                Timber.v("XXX Saved job ${itemChange.item()}")
                sendBlocking(itemChange.item())
            },
            { exception ->
                Timber.e(exception, "XXX Save job failed: ${exception.message}")
                cancel(CancellationException("XXX Error", exception))
                throw exception
            })
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override fun getJob(jobID: String) = callbackFlow<Job> {
        Timber.v("XXX getJob $jobID")
        Amplify.DataStore.query(Job::class.java, Where.matches(Job.ID.eq(jobID)),
            { jobs ->
                val existingJob = jobs.next()
                Timber.v("XXX Found existing job $existingJob")
                sendBlocking(existingJob)
            },
            { exception ->
                Timber.e(exception, "XXX Get job failed: ${exception.message}")
                cancel(CancellationException("XXX Error", exception))
                throw exception
            })
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override fun deleteJob(job: Job) = callbackFlow<Unit> {
        Timber.v("XXX Deleting Job")
        kotlin.runCatching {
            Amplify.DataStore.delete(job, { itemChange ->
                Timber.v("XXX Success deleting job ${itemChange.item()}")
            }, { exception ->
                Timber.v(exception, "XXX Error deleting job ${exception.message}")
                cancel(CancellationException("XXX Error", exception))
                throw exception
            })
        }.onFailure { throw it }
        awaitClose { }
    }
}
