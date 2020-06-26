package com.servicetitan.awsappsyncpoc.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Job
import com.amplifyframework.datastore.generated.model.JobStatus
import com.servicetitan.awsappsyncpoc.R
import kotlinx.android.synthetic.main.item_job_card.view.*

const val JOB_VIEW_TYPE = 0
const val ADD_BUTTON_VIEW_TYPE = 1

class MainRecyclerViewAdapter(private val context: Context, private val callbacks: Callbacks) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) {
            return ADD_BUTTON_VIEW_TYPE
        } else {
            JOB_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == JOB_VIEW_TYPE) {
            JobCardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_job_card, parent, false)
            )
        } else {
            AddJobButtonViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_job_add_button, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is JobCardViewHolder) {
            with(items[position] as Job) {
                holder.jobIDTextView.text = id
                holder.jobNameTextView.text = owner

                holder.colorBarView.background = ContextCompat.getDrawable(
                    context,
                    when (status) {
                        JobStatus.SCHEDULED -> R.color.yellow
                        JobStatus.DISPATCHED -> R.color.green
                        JobStatus.COMPLETED -> R.color.gray
                        else -> R.color.gray
                    }
                )

                holder.scheduledButton.setOnClickListener {
                    callbacks.onScheduledButtonClicked(
                        id, holder.jobNameTextView.text.toString()
                    )
                }
                holder.dispatchedButton.setOnClickListener {
                    callbacks.onDispatchedButtonClicked(
                        id, holder.jobNameTextView.text.toString()
                    )
                }
                holder.completedButton.setOnClickListener {
                    callbacks.onCompletedButtonClicked(
                        id, holder.jobNameTextView.text.toString()
                    )
                }

                holder.closeButton.setOnClickListener { callbacks.closeJobButtonClicked(this) }
            }
        } else {
            (holder as AddJobButtonViewHolder).let {
                it.addButton.setOnClickListener { callbacks.addNewJobButtonClicked() }
            }
        }
    }

    inner class JobCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorBarView: View = itemView.color_bar

        val jobIDTextView: TextView = itemView.findViewById(R.id.job_id_value_text_view)
        val jobNameTextView: TextView = itemView.findViewById(R.id.job_name_value_edit_text)

        val scheduledButton: Button = itemView.findViewById(R.id.scheduled_button)
        val dispatchedButton: Button = itemView.findViewById(R.id.dispatched_button)
        val completedButton: Button = itemView.findViewById(R.id.completed_button)

        val closeButton: View = itemView.findViewById(R.id.close_job_button)
    }

    inner class AddJobButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: View = itemView.findViewById(R.id.add_job_button)
    }

    fun updateJobs(newJobs: MutableList<Job>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return items.size
            }

            override fun getNewListSize(): Int {
                return newJobs.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (items[oldItemPosition] as Job).id == newJobs[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newJobs[newItemPosition]
            }
        })

        items.clear()
        items.addAll(newJobs)
        diffResult.dispatchUpdatesTo(this)
    }

    interface Callbacks {
        fun onScheduledButtonClicked(jobId: String, owner: String)
        fun onDispatchedButtonClicked(jobId: String, owner: String)
        fun onCompletedButtonClicked(jobId: String, owner: String)

        fun addNewJobButtonClicked()
        fun closeJobButtonClicked(job: Job)
    }
}
