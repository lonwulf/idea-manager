package com.lonwulf.ideamanager.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "task_item", indices = [Index(value = ["id"], unique = true)])
data class TaskItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "time_range")
    val timeRange: String,
    @ColumnInfo(name = "status")
    val status: Boolean,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date")
    val date: LocalDate
) : BaseEntity()
