package com.lonwulf.ideamanager.application

import android.app.Application
import androidx.work.Configuration
import com.lonwulf.ideamanager.core.BuildConfig
import com.lonwulf.ideamanager.core.di.modules.coreModule
import com.lonwulf.ideamanager.di.mainAppModule
import com.lonwulf.ideamanager.taskmanager.di.module.taskManagerModule
import com.lonwulf.ideamanager.util.createNotificationChannel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.Executors

class IdeaManagerApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@IdeaManagerApplication)
            modules(mainAppModule, coreModule, taskManagerModule)
        }
        createNotificationChannel(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setExecutor(Executors.newFixedThreadPool(8))
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()
}