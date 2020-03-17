package com.prismosis.checklist.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.prismosis.checklist.R
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.utils.Utils

/**
 * Created by Ehsan Saddique on 2020-03-14
 */

class TaskListAdapter(private var tasks: List<DTOTask>, private var listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.task_item, parent, false)

        return TaskItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = tasks.get(position)
        val taskViewHolder = holder as TaskItemViewHolder
        taskViewHolder.bind(task, listener)
    }

    fun setTaks(tasks: List<DTOTask>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

}

class TaskItemViewHolder(itemView: View, isTaskDetail: Boolean = false) : RecyclerView.ViewHolder(itemView) {
    private var taskName: TextView
    private var taskDescription: TextView
    private var startDate: TextView
    private var endDate: TextView
    private var subTasks: TextView
    private var taskStatus: TextView
    private var isSynced: ImageView
    private var toolbar: Toolbar

    init {
        taskName = itemView.findViewById(R.id.task_name)
        taskDescription = itemView.findViewById(R.id.task_description)
        startDate = itemView.findViewById(R.id.task_start_date)
        endDate = itemView.findViewById(R.id.task_end_date)
        subTasks = itemView.findViewById(R.id.sub_tasks)
        taskStatus = itemView.findViewById(R.id.task_status)
        isSynced = itemView.findViewById(R.id.is_synced)
        toolbar = itemView.findViewById(R.id.toolbar_task_item)
        if (isTaskDetail) {
            toolbar.inflateMenu(R.menu.menu_task_item_detail)
        }
        else {
            toolbar.inflateMenu(R.menu.menu_task_item)
        }
    }

    fun bind(task: DTOTask, listener: ClickListener) {
        taskName.setText(task.taskName)
        taskDescription.setText(if (task.taskDescription.isNullOrEmpty()) "No description" else task.taskDescription)
        startDate.setText("Starts: " + Utils.instance.stringFromDate(task.startDate))
        endDate.setText("Ends: " + Utils.instance.stringFromDate(task.endDate))
        subTasks.setText("+ Sub Tasks: " + task.subTasksCount)
        taskStatus.setText(task.status.string)
        taskStatus.setBackgroundResource(task.status.drawableId)
        isSynced.visibility = if (task.isDirty) View.VISIBLE else View.GONE

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

        itemView.setOnClickListener {
            listener.onItemClick(task)
        }
    }
}

interface ClickListener {
    fun onItemClick(task: DTOTask)
    fun onEditClick(task: DTOTask)
    fun onDeleteClick(task: DTOTask)
    fun onChangeStatusClick(task: DTOTask)
}