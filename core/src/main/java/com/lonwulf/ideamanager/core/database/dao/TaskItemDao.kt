package com.lonwulf.ideamanager.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lonwulf.ideamanager.core.database.entity.TaskItemEntity

@Dao
interface TaskItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: TaskItemEntity): Long

    @Query("SELECT * FROM task_item ORDER BY created_at DESC LIMIT 10")
    fun getTasks(): List<TaskItemEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entityData: TaskItemEntity)

    @Query("UPDATE task_item SET status = :status WHERE id = :id")
    fun updateTaskStatus(status: Boolean, id: Int): Int

    @Transaction
    fun updateTask(entityData: TaskItemEntity): Int {
        return if (getTask(entityData.id) != null) {
            updateTaskStatus(entityData.status, entityData.id)
//            update(entityData)
            1
        } else {
            0
        }
    }

    @Query("SELECT * FROM task_item WHERE id = :id")
    fun getTask(id: Int): TaskItemEntity

    @Query("DELETE FROM task_item")
    fun deleteAll()

    @Query("DELETE FROM task_item WHERE id = :id")
    fun deleteTask(id: Int): Int
}