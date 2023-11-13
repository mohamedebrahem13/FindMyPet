package com.example.petme

import android.app.Application
import androidx.work.*
import com.example.petme.data.worker.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshDataRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(
                repeatInterval = 1, // repeat every 1 day
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .build()

        val uniqueWorkName = "refresh_data_unique_work"

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.KEEP, // or ExistingPeriodicWorkPolicy.REPLACE
            refreshDataRequest
        )
    }
}
