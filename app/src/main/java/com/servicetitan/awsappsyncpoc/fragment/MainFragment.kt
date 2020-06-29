package com.servicetitan.awsappsyncpoc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import com.servicetitan.awsappsyncpoc.databinding.FragmentMainBinding
import com.servicetitan.awsappsyncpoc.di.provideComponent
import com.servicetitan.awsappsyncpoc.viewmodel.MainViewModel
import timber.log.Timber

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>(),
    MainRecyclerViewAdapter.Callbacks {

    private val jobs = linkedMapOf<String, Job>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.mainRecyclerView.adapter = MainRecyclerViewAdapter(context!!, this)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun observeViewModel() {
        viewModel.getJobs().observe(viewLifecycleOwner, Observer { updatedJobs ->
            Timber.v("Fragment got job update $updatedJobs")

            jobs.clear()
            updatedJobs.forEach { jobs[it.id] = it }

            (binding.mainRecyclerView.adapter as MainRecyclerViewAdapter).updateJobs(jobs.values.toMutableList())
        })

        viewModel.init()
    }

    override fun onScheduledButtonClicked(jobId: String, owner: String) {
        viewModel.updateJobStatus(jobId, JobStatus.SCHEDULED)
    }

    override fun onDispatchedButtonClicked(jobId: String, owner: String) {
        viewModel.updateJobStatus(jobId, JobStatus.DISPATCHED)
    }

    override fun onCompletedButtonClicked(jobId: String, owner: String) {
        viewModel.updateJobStatus(jobId, JobStatus.COMPLETED)
    }

    override fun saveJobButtonClicked(jobId: String, owner: String) {
        viewModel.updateJobOwner(jobId, owner)
    }

    override fun addNewJobButtonClicked() {
        viewModel.addNewJob()
    }

    override fun closeJobButtonClicked(job: Job) {
        viewModel.deleteJob(job)
    }

    override fun inject() {
        provideComponent()?.inject(this)
    }

    override fun viewModelClass() = MainViewModel::class.java
}