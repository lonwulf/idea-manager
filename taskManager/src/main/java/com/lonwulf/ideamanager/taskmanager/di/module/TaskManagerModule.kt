package com.lonwulf.ideamanager.taskmanager.di.module

import com.lonwulf.ideamanager.taskmanager.data.source.TaskRepositoryImpl
import com.lonwulf.ideamanager.taskmanager.domain.repository.TaskRepository
import com.lonwulf.ideamanager.taskmanager.domain.usecase.DeleteTaskUseCase
import com.lonwulf.ideamanager.taskmanager.domain.usecase.InsertTaskUseCase
import com.lonwulf.ideamanager.taskmanager.domain.usecase.UpdateTaskUseCase
import com.lonwulf.ideamanager.taskmanager.presentation.viewmodel.TaskManagerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskManagerModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single { DeleteTaskUseCase() }
    single { UpdateTaskUseCase() }
    single { InsertTaskUseCase() }
    viewModel { TaskManagerViewModel(get(), get(), get()) }


}