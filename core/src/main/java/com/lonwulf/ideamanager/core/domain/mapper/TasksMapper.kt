package com.lonwulf.ideamanager.core.domain.mapper

import com.lonwulf.ideamanager.core.database.entity.TaskItemEntity
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import java.time.LocalDate

fun List<TaskItemEntity>.mapEntityListToDomainList(): List<TaskItem> =
    mutableListOf<TaskItem>().apply {
        this@mapEntityListToDomainList.map {
            add(it.mapEntityToDomain())
        }
    }

fun TaskItemEntity.mapEntityToDomain(): TaskItem =
    TaskItem(
        icon = this.icon,
        title = this.title,
        timeRange = this.timeRange,
        description = this.description,
        date = this.date,
        category = this.category,
        id = this.id,
        status = this.status
    )

fun TaskItem.mapDomainToEntity(): TaskItemEntity = TaskItemEntity(
    icon = this.icon ?: "",
    title = this.title ?: "",
    timeRange = this.timeRange ?: "",
    status = this.status ?: false,
    category = this.category ?: "",
    date = this.date?: LocalDate.now(),
    description = this.description?:"",
    id = this.id ?: 0
)