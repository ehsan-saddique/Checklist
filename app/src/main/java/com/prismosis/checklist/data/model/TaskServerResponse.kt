package com.prismosis.checklist.data.model

/**
 * Created by Ehsan Saddique on 2020-03-16
 */

class TaskServerResponseWrapper {
    var name = ""
    var fields = TaskServerResponse()
}

class TaskServerResponse {
    var taskId = StringValue()
    var parentId = StringValue()
    var userId = StringValue()
    var taskName = StringValue()
    var taskDescription = StringValue()
    var createdAt = IntegerValue()
    var startDate = IntegerValue()
    var endDate = IntegerValue()
    var status = IntegerValue()
    var isDirty = IntegerValue()
    var isDeleted = IntegerValue()
}

class StringValue {
    var stringValue: String = ""
}

class IntegerValue {
    var integerValue: String = ""
}