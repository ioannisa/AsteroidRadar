package com.udacity.asteroidradar.utils

import android.app.Application
import androidx.work.*
import com.udacity.asteroidradar.work.RefreshAsteroidDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication: Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val workerConstraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshAsteroidDataWorker>(1, TimeUnit.DAYS)
                .setConstraints(workerConstraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                RefreshAsteroidDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest)
    }
}