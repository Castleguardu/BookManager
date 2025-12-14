package com.plcoding.material3expressiveguide.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import com.plcoding.material3expressiveguide.IBookManager
import com.plcoding.material3expressiveguide.INewBookArrivedListener
import com.plcoding.material3expressiveguide.data.AppDatabase
import com.plcoding.material3expressiveguide.data.Book
import com.plcoding.material3expressiveguide.data.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.CopyOnWriteArrayList

class BookManagerService : Service() {

    private val TAG = "BookManagerService"
    private lateinit var database: AppDatabase
    
    // RemoteCallbackList is designed for AIDL callbacks to handle death recipients automatically
    private val mListenerList = RemoteCallbackList<INewBookArrivedListener>()
    
    // Service Scope for background tasks if needed (though Binder threads are background)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val mBinder = object : IBookManager.Stub() {
        override fun getBookList(): List<Book> {
            Log.d(TAG, "getBookList called from ${getCallingPid()}")
            return database.bookDao().getAll()
        }

        override fun addBook(book: Book) {
            Log.d(TAG, "addBook called: $book")
            // Prevent duplicates (simple check by Title + Author)
            val exists = database.bookDao().count(book.title, book.author) > 0
            if (!exists) {
                database.bookDao().insert(book)
                onNewBookArrived(book)
            } else {
                Log.d(TAG, "Book already exists, skipping: ${book.title}")
            }
        }
        
        override fun deleteBook(book: Book) {
             Log.d(TAG, "deleteBook called: $book")
             // Note: Room delete usually requires the primary key to match.
             // Ensure the passed book object has the correct ID.
             database.bookDao().delete(book)
             // In a real app, we might want a 'onBookDeleted' callback too.
        }

        override fun getNotesForBook(bookId: Int): List<Note> {
            Log.d(TAG, "getNotesForBook called: bookId=$bookId from ${getCallingPid()}")
            return database.noteDao().getNotesForBookSync(bookId)
        }

        override fun addNote(note: Note) {
            Log.d(TAG, "addNote called: $note")
            database.noteDao().insertNoteSync(note)
        }

        override fun deleteNote(note: Note) {
            Log.d(TAG, "deleteNote called: $note")
            database.noteDao().deleteNoteSync(note)
        }

        override fun registerListener(listener: INewBookArrivedListener?) {
            mListenerList.register(listener)
            Log.d(TAG, "registerListener success. Current count: ${mListenerList.registeredCallbackCount}")
        }

        override fun unregisterListener(listener: INewBookArrivedListener?) {
            mListenerList.unregister(listener)
            Log.d(TAG, "unregisterListener success. Current count: ${mListenerList.registeredCallbackCount}")
        }
    }

    private fun onNewBookArrived(book: Book) {
        val count = mListenerList.beginBroadcast()
        for (i in 0 until count) {
            try {
                val listener = mListenerList.getBroadcastItem(i)
                listener.onNewBookArrived(book)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mListenerList.finishBroadcast()
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        // Insert some dummy data if empty (optional)
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }
}

