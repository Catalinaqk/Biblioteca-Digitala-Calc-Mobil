package com.example.taulibrary.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val bookId: String,
    val reviewText: String,
    val rating: Float
)