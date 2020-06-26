package com.servicetitan.awsappsyncpoc.di

import com.servicetitan.awsappsyncpoc.repository.JobRepository
import com.servicetitan.awsappsyncpoc.repository.STJobRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindJobRepository(repository: STJobRepository): JobRepository
}
