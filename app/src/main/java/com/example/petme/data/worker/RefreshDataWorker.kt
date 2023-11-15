package com.example.petme.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.petme.domain.usecase.firebaseUseCase.worker.RefreshDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshDataUseCase: RefreshDataUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            Log.d(TAG, "Starting background work...")

            // Your data refresh logic using the use case
            refreshDataUseCase.execute()

            Log.d(TAG, "Background work completed successfully.")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Background work failed with exception: $e")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "RefreshDataWorker"
    }
}