package com.done.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RoundEntity::class, PlayerEntity::class, ScoreEntity::class],
    version = 1, exportSchema = true
)

@TypeConverters(DbConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao
}
