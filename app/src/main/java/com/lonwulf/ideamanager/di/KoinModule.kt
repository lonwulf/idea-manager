package com.lonwulf.ideamanager.di

import com.lonwulf.ideamanager.data.source.TaskRepositoryImpl
import com.lonwulf.ideamanager.domain.repository.TaskRepository
import com.lonwulf.ideamanager.domain.usecase.GetTasksUseCase
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainAppModule = module {
    viewModel { SharedViewModel(get()) }
    single { GetTasksUseCase() }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
}