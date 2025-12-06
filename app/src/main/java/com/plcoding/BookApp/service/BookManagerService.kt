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
            database.bookDao().insert(book)
            onNewBookArrived(book)
        }
        
        override fun deleteBook(book: Book) {
             Log.d(TAG, "deleteBook called: $book")
             // Note: Room delete usually requires the primary key to match.
             // Ensure the passed book object has the correct ID.
             database.bookDao().delete(book)
             // In a real app, we might want a 'onBookDeleted' callback too.
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

