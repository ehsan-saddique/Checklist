package com.prismosis.checklist.utils

import com.prismosis.checklist.R

/**
 * Created by Ehsan Saddique on 2020-03-14
 */

class Enum {


    enum class TaskStatus constructor(val value: Int) {
        PENDING(1),
        INPROGRESS(2),
        COMPLETED(3),
        EXPIRED(4);

        val string: String
            get() {
                var stringValue = "--"

                when (this) {
                    PENDING -> stringValue = "Pending"
                    INPROGRESS -> stringValue = "In Progress"
                    COMPLETED -> stringValue = "Completed"
                    EXPIRED -> stringValue = "Expired"
                }

                return stringValue
            }

        val drawableId: Int
            get() {
                var id = 0

                when (this) {
                    PENDING -> id = R.drawable.task_status_pending
                    INPROGRESS -> id = R.drawable.task_status_inprogress
                    COMPLETED -> id = R.drawable.task_status_completed
                    EXPIRED -> id = R.drawable.task_status_expired
                }

                return id
            }

        companion object {
            fun getValueFromInt(value: Int): TaskStatus {
                for (taskStatus in TaskStatus.values()) {
                    if (taskStatus.value == value) {
                        return taskStatus
                    }
                }
                return PENDING
            }

            fun getValueFromString(value: String): TaskStatus {
                for (taskStatus in TaskStatus.values()) {
                    if (taskStatus.string.equals(value)) {
                        return taskStatus
                    }
                }
                return PENDING
            }
        }
    }
}