package com.plcoding.material3expressiveguide.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.material3expressiveguide.IBookManager
import com.plcoding.material3expressiveguide.INewBookArrivedListener
import com.plcoding.material3expressiveguide.data.Book
import com.plcoding.material3expressiveguide.data.Note
import com.plcoding.material3expressiveguide.service.BookManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.plcoding.material3expressiveguide.utils.DoubanScraper

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "BookViewModel"

    private var iBookManager: IBookManager? = null
    private var isBound = false

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _serviceConnected = MutableStateFlow(false)
    val serviceConnected: StateFlow<Boolean> = _serviceConnected.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    // Login State: null = not logged in, true = admin, false = user
    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service Connected")
            iBookManager = IBookManager.Stub.asInterface(service)
            isBound = true
            _serviceConnected.value = true
            
            try {
                // Register listener for updates
                iBookManager?.registerListener(bookArrivedListener)
                // Initial fetch
                fetchBooks()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service Disconnected")
            iBookManager = null
            isBound = false
            _serviceConnected.value = false
        }
    }

    private val bookArrivedListener = object : INewBookArrivedListener.Stub() {
        override fun onNewBookArrived(newBook: Book?) {
            Log.d(TAG, "New book arrived: $newBook")
            // This comes from a binder thread, need to refresh data
            // Since our list might be stale, simpler to re-fetch all for consistency, 
            // or append if we maintain local list.
            // For simplicity and correctness with DB, let's re-fetch.
            viewModelScope.launch(Dispatchers.IO) {
                fetchBooks()
            }
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent(getApplication(), BookManagerService::class.java)
        getApplication<Application>().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun fetchBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = iBookManager?.bookList ?: emptyList()
                _books.value = list
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun fetchNotes(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = iBookManager?.getNotesForBook(bookId) ?: emptyList()
                _notes.value = list
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun addNote(bookId: Int, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val note = Note(bookId = bookId, content = content)
                iBookManager?.addNote(note)
                fetchNotes(bookId)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDoubanBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scrapedBooks = DoubanScraper.scrapeTop250(getApplication())
                for (book in scrapedBooks) {
                    try {
                        iBookManager?.addBook(book)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
                // Refresh list
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Error fetching Douban: ${e.message}")
            }
        }
    }

    fun addBook(title: String, author: String, price: Double, description: String = "") {
        if (_isAdmin.value != true) return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: Add support for cover image URI later
                val book = Book(
                    title = title, 
                    author = author, 
                    price = price, 
                    description = description,
                    status = 0 // Default to unread
                )
                iBookManager?.addBook(book)
                // onNewBookArrived will trigger refresh
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteBook(book: Book) {
        if (_isAdmin.value != true) return
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                iBookManager?.deleteBook(book)
                fetchBooks() // Manually refresh as we didn't add a delete listener
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun login(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
    }
    
    fun logout() {
        _isAdmin.value = null
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound && iBookManager != null && iBookManager!!.asBinder().isBinderAlive) {
            try {
                iBookManager?.unregisterListener(bookArrivedListener)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }
}
