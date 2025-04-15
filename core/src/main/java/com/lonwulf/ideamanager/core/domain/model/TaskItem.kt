package com.lonwulf.ideamanager.core.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

data class TaskItem(
    val id: Int? = null,
    val icon: String? = "",
    val title: String? = "",
    val description: String? = "",
    val timeRange: String? = "",
    val status: Boolean? = false,
    val category: String? = "",
    val date: LocalDate? = null
)

data class MonthData(val month: Month, val name: String, val isSelected: Boolean = false)
data class DayStatistic(
    val day: String,
    val dayOfWeek: DayOfWeek,
    val count: Int,
    val isSelected: Boolean = false
)