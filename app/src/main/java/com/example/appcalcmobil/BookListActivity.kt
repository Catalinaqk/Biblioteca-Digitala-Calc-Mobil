package com.example.appcalcmobil

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcalcmobil.adapter.BookAdapter
import com.example.appcalcmobil.api.RetrofitInstance
import kotlinx.coroutines.launch

class BookListActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var adapter: BookAdapter
    private lateinit var etSearch: EditText
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        // 1. Legătura cu elementele din XML
        etSearch = findViewById(R.id.etSearch)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val rvBooks = findViewById<RecyclerView>(R.id.rvBooks)

        // 2. Configurare RecyclerView (Lista)
        rvBooks.layoutManager = LinearLayoutManager(this)

        // AICI ESTE SCHIMBAREA IMPORTANTĂ: Trimitem toate datele prin Intent
        adapter = BookAdapter(emptyList()) { book ->
            val intent = Intent(this, BookDetailActivity::class.java)

            // Trimitem ID-ul și Titlul
            intent.putExtra("BOOK_ID", book.id)
            intent.putExtra("BOOK_TITLE", book.volumeInfo.title)

            // Trimitem Autorii (unim lista într-un string)
            val authors = book.volumeInfo.authors?.joinToString(", ") ?: "Autor necunoscut"
            intent.putExtra("BOOK_AUTHOR", authors)

            // Trimitem Descrierea
            val description = book.volumeInfo.description ?: "Nu există descriere disponibilă."
            intent.putExtra("BOOK_DESC", description)

            // Trimitem Rating-ul (convertim la float)
            val rating = book.volumeInfo.averageRating?.toFloat() ?: 0f
            intent.putExtra("BOOK_RATING", rating)

            // Trimitem Imaginea (URL) - Corectăm http în https
            val imageUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:")
            intent.putExtra("BOOK_IMAGE", imageUrl)

            startActivity(intent)
        }
        rvBooks.adapter = adapter

        // 3. Configurare Buton Căutare (Retrofit)
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isNotEmpty()) {
                searchBooksOnline(query)
            } else {
                Toast.makeText(this, "Scrie ceva pentru a căuta!", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Configurare Senzori (Accelerometru pentru Shake)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // Funcția care face cererea la Google Books API
    private fun searchBooksOnline(query: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.searchBooks(query)
                if (response.items != null) {
                    adapter.updateList(response.items)
                } else {
                    Toast.makeText(this@BookListActivity, "Nu s-au găsit cărți.", Toast.LENGTH_SHORT).show()
                    adapter.updateList(emptyList())
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookListActivity, "Eroare rețea: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- Logica pentru Senzori (Shake to Clear) ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculăm accelerația totală (eliminăm gravitația)
            val acceleration = kotlin.math.sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH

            if (acceleration > 12) { // Pragul pentru scuturare
                etSearch.setText("")
                Toast.makeText(this, "Câmp șters prin scuturare!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nu avem nevoie de implementare aici
    }

    override fun onResume() {
        super.onResume()
        // Pornim senzorul când aplicația e activă
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        // Oprim senzorul când aplicația e în pauză pentru a economisi baterie
        sensorManager.unregisterListener(this)
    }
}