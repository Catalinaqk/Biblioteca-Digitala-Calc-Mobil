package com.example.appcalcmobil.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taulibrary.database.ReviewEntity


@Database(entities = [ReviewEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}