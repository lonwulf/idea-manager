package com.lonwulf.ideamanager.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lonwulf.ideamanager.core.database.converter.Converters
import com.lonwulf.ideamanager.core.database.dao.TaskItemDao
import com.lonwulf.ideamanager.core.database.entity.TaskItemEntity

@Database(
    entities = [
        TaskItemEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class IdeaManagerDatabase : RoomDatabase() {

    abstract fun tasksItemDao(): TaskItemDao

}