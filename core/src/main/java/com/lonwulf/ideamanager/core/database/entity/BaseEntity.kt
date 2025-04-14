package com.lonwulf.ideamanager.core.database.entity

import androidx.room.ColumnInfo
import java.io.Serializable
import java.util.Date

abstract class BaseEntity : Serializable {

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    var createdAt: Date = Date(System.currentTimeMillis())

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    var updatedAt: Date = Date(System.currentTimeMillis())
}