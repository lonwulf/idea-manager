package com.lonwulf.ideamanager.core.domain.mapper

import com.lonwulf.ideamanager.core.database.entity.TaskItemEntity
import com.lonwulf.ideamanager.core.domain.model.TaskItem

fun List<TaskItemEntity>.mapEntityListToDomainList(): List<TaskItem> =
    mutableListOf<TaskItem>().apply {
        this@mapEntityListToDomainList.map {
            add(it.mapEntityToDomain())
        }
    }

fun TaskItemEntity.mapEntityToDomain(): TaskItem =
    TaskItem(icon = this.icon, title = this.title, timeRange = this.timeRange)

fun TaskItem.mapDomainToEntity(): TaskItemEntity = TaskItemEntity(
    icon = this.icon ?: "",
    title = this.title ?: "",
    timeRange = this.timeRange ?: "",
    status = this.status ?: false,
    category = this.category ?: "",
    date = this.date,
    id = this.id ?: 0
)