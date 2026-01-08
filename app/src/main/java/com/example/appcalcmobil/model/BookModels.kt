package com.example.appcalcmobil.model

data class BookResponse(val items: List<BookItem>?)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val averageRating: Double?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(val thumbnail: String?)