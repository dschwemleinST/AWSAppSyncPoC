package com.servicetitan.awsappsyncpoc.repository

import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface JobRepository {
    fun queryCurrentJobs(): Observable<Job>
    fun observeJobChanges(): Observable<DataStoreItemChange<Job>>
    fun createOrUpdateJob(optionalJobId: String?, status: JobStatus, owner: String): Single<Job>
    fun deleteJob(job: Job): Completable
}
