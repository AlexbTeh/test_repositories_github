package com.done.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RoundEntity::class, PlayerEntity::class, ScoreEntity::class],
    version = 1, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao
}
