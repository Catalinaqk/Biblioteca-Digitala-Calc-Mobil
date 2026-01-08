package com.example.appcalcmobil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.appcalcmobil.database.AppDatabase
import com.example.taulibrary.database.ReviewEntity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.ImageView
import com.bumptech.glide.Glide

class BookDetailActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Baza de date
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "library-db").build()

        // Conectare elemente din XML
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvAuthor = findViewById<TextView>(R.id.tvDetailAuthor)
        val tvDesc = findViewById<TextView>(R.id.tvDetailDesc)
        val tvRating = findViewById<TextView>(R.id.tvDetailRating)
        val ivCover = findViewById<ImageView>(R.id.ivDetailCover)
        val etReview = findViewById<EditText>(R.id.etReview)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnDownload = findViewById<Button>(R.id.btnDownload)

        // Preluare date din Intent (trimise din lista)
        val bookId = intent.getStringExtra("BOOK_ID") ?: ""
        val title = intent.getStringExtra("BOOK_TITLE")
        val author = intent.getStringExtra("BOOK_AUTHOR")
        val desc = intent.getStringExtra("BOOK_DESC")
        val rating = intent.getFloatExtra("BOOK_RATING", 0f)
        val imageUrl = intent.getStringExtra("BOOK_IMAGE")

        // Afisare date pe ecran
        tvTitle.text = title
        tvAuthor.text = "de $author"
        tvDesc.text = desc
        tvRating.text = "Rating: $rating / 5"

        // Imagine cu Glide
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(ivCover)
        }

        // Buton inapoi
        btnBack.setOnClickListener {
            finish() // merge inapoi la lista
        }

        // Incarcare recenzie
        lifecycleScope.launch(Dispatchers.IO) {
            val existing = db.bookDao().getReview(bookId)
            if (existing != null) {
                runOnUiThread { etReview.setText(existing.reviewText) }
            }
        }

        // Salvare recenzie
        btnSave.setOnClickListener {
            val text = etReview.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                db.bookDao().insertReview(ReviewEntity(bookId, text, 5.0f))
                runOnUiThread {
                    Toast.makeText(this@BookDetailActivity, "Recenzie salvată!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Download
        btnDownload.setOnClickListener {
            val serviceIntent = Intent(this, DownloadService::class.java)
            startService(serviceIntent)
        }
    }
}