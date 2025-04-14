package com.lonwulf.ideamanager.core.di.modules

import com.lonwulf.ideamanager.core.database.IdeaManagerDatabase
import com.lonwulf.ideamanager.core.database.dao.TaskItemDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single<IdeaManagerDatabase> {
        provideDatabase(androidContext())
    }
    single<TaskItemDao> { get<IdeaManagerDatabase>().tasksItemDao() }

}