package com.servicetitan.awsappsyncpoc.repository

import com.amplifyframework.datastore.DataStoreItemChange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class STJobRepository @Inject constructor() : JobRepository {
    override fun queryCurrentJobs(): Flow<Any> {
        TODO("Not yet implemented")
    }

    override fun observeJobChanges(): Flow<Any> {
        TODO("Not yet implemented")
    }

    override fun getJob(jobID: String): Flow<Any> {
        TODO("Not yet implemented")
    }

    override fun saveJob(job: Any): Flow<Any> {
        TODO("Not yet implemented")
    }

    override fun deleteJob(job: Any): Flow<Unit> {
        TODO("Not yet implemented")
    }
}
