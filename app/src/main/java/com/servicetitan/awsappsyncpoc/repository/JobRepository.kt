package com.servicetitan.awsappsyncpoc.repository

import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.Job
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun queryCurrentJobs(): Flow<Job>
    fun observeJobChanges(): Flow<DataStoreItemChange<Job>>

    fun getJob(jobID: String): Flow<Job>
    fun saveJob(job: Job): Flow<Job>
    fun deleteJob(job: Job): Flow<Unit>
}
