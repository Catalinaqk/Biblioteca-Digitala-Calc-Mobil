package com.example.appcalcmobil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcalcmobil.R
import com.example.appcalcmobil.model.BookItem

class BookAdapter(
    private var books: List<BookItem>,
    private val onClick: (BookItem) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val author: TextView = view.findViewById(R.id.tvAuthor)
        val image: ImageView = view.findViewById(R.id.ivCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.title.text = book.volumeInfo.title
        holder.author.text = book.volumeInfo.authors?.joinToString(", ") ?: "Autor necunoscut"

        // Încărcare imagine cu Glide
        val url = book.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:")
        if (url != null) {
            Glide.with(holder.itemView.context).load(url).into(holder.image)
        }

        holder.itemView.setOnClickListener { onClick(book) }
    }

    override fun getItemCount() = books.size

    fun updateList(newBooks: List<BookItem>) {
        books = newBooks
        notifyDataSetChanged()
    }
}