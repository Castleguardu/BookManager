package com.plcoding.material3expressiveguide.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): List<Book>

    @Insert
    fun insert(book: Book)

    @Delete
    fun delete(book: Book)

    @Query("SELECT COUNT(*) FROM books WHERE title = :title AND author = :author")
    fun count(title: String, author: String): Int
}

