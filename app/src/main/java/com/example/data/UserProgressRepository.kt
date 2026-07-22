package com.example.data

import kotlinx.coroutines.flow.Flow

class UserProgressRepository(private val userProgressDao: UserProgressDao) {
    val userProgressFlow: Flow<UserProgress?> = userProgressDao.getUserProgressFlow()

    suspend fun getProgress(): UserProgress? = userProgressDao.getUserProgress()

    suspend fun saveProgress(progress: UserProgress) {
        userProgressDao.insertOrUpdate(progress)
    }

    suspend fun clearProgress() {
        userProgressDao.clearAll()
        // Save initial default progress
        userProgressDao.insertOrUpdate(UserProgress())
    }
}
