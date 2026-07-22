package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Singleton row for the local player
    val googleId: String = "",
    val name: String = "Guest Player",
    val email: String = "",
    val level: Int = 1,
    val xp: Int = 0, // 0 to 100 XP per level
    val coins: Int = 100, // Starts with some coins for the store
    val shieldsCount: Int = 1,
    val hintsCount: Int = 2,
    val extraTimeCount: Int = 1,
    val isTutorialCompleted: Boolean = false,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val isVipActive: Boolean = false,
    val playedImageIds: String = "", // Comma-separated played image IDs to track history and prevent duplicates
    val claimedMilestones: String = "", // Comma-separated list of claimed milestone levels e.g. "2,3"
    val equippedAvatarId: Int = 0,
    val unlockedAvatarIds: String = "0",

    // Interrupted Game State Tracking
    val activeLevel: Int = -1, // -1 means no active game in progress
    val activeImageId: Int = -1,
    val activeQuestionIndex: Int = 0,
    val activeClarity: Int = 0, // Starts at 0% clarity (100% blurred)
    val activeStreak: Int = 0,
    val activeWrongStreak: Int = 0,
    val activeTimerSecondsLeft: Int = 120
)
