package com.servicetitan.awsappsyncpoc.repository

import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun queryCurrentJobs(): Flow<Any>
    fun observeJobChanges(): Flow<Any>

    fun getJob(jobID: String): Flow<Any>
    fun saveJob(job: Any): Flow<Any>
    fun deleteJob(job: Any): Flow<Unit>
}
