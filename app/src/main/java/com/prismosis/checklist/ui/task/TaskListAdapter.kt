package com.prismosis.checklist.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.prismosis.checklist.R
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.utils.Utils

/**
 * Created by Ehsan Saddique on 2020-03-14
 */

class TaskListAdapter(private var tasks: List<Task>, private var listener: ClickListener) : RecyclerView.Adapter<TaskListAdapter.TaskItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.task_item, parent, false)

        return TaskItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        val task = tasks.get(position)
        holder.bind(task, listener)
    }

    fun setTaks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


    class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var taskName: TextView
        private var taskDescription: TextView
        private var startDate: TextView
        private var endDate: TextView
        private var subTasks: TextView
        private var toolbar: Toolbar

        init {
            taskName = itemView.findViewById(R.id.task_name)
            taskDescription = itemView.findViewById(R.id.task_description)
            startDate = itemView.findViewById(R.id.task_start_date)
            endDate = itemView.findViewById(R.id.task_end_date)
            subTasks = itemView.findViewById(R.id.sub_tasks)
            toolbar = itemView.findViewById(R.id.toolbar_task_item)
            toolbar.inflateMenu(R.menu.menu_task_item)
        }

        fun bind(task: Task, listener: ClickListener) {
            taskName.setText(task.name)
            taskDescription.setText(if (task.description.isNullOrEmpty()) "No description" else task.description)
            startDate.setText("Starts: " + Utils.stringFromDate(task.startDate))
            endDate.setText("Ends: " + Utils.stringFromDate(task.endDate))
            subTasks.setText("Sub Tasks: 0")

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        listener.onEditClick(task)
                        true
                    }
                    R.id.action_change_status -> {
                        listener.onChangeStatusClick(task)
                        true
                    }
                    R.id.action_delete -> {
                        listener.onDeleteClick(task)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    interface ClickListener {
        fun onItemClick(task: Task)
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onChangeStatusClick(task: Task)
    }

}