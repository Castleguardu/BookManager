package com.plcoding.material3expressiveguide.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getNotesForBook(bookId: Int): Flow<List<Note>>

    // For cross-process Service (Binder) calls: synchronous APIs (Binder threads are background)
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getNotesForBookSync(bookId: Int): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNoteSync(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    fun deleteNoteSync(note: Note)
}

