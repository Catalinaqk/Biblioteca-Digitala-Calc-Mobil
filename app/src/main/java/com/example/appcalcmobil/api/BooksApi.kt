package com.example.appcalcmobil.api
import com.example.appcalcmobil.model.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {
    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): BookResponse
}