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
import com.plcoding.material3expressiveguide.service.BookManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "BookViewModel"

    private var iBookManager: IBookManager? = null
    private var isBound = false

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _serviceConnected = MutableStateFlow(false)
    val serviceConnected: StateFlow<Boolean> = _serviceConnected.asStateFlow()
    
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

    fun fetchDoubanBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "https://book.douban.com/top250"
                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get()
                
                val elements = doc.select("tr.item")
                for (element in elements) {
                    try {
                        // Title
                        val titleElement = element.select("div.pl2 a")
                        val title = titleElement.attr("title").ifEmpty { titleElement.text().replace("\n", "").trim() }
                        
                        // Cover
                        val imgElement = element.select("a.nbg img")
                        val coverUrl = imgElement.attr("src")
                        
                        // Info (Author, Price, etc.)
                        val infoText = element.select("p.pl").text()
                        val parts = infoText.split("/")
                        val author = parts.firstOrNull()?.trim() ?: "Unknown"
                        val priceStr = parts.lastOrNull()?.trim()
                        val price = priceStr?.replace("元", "")?.replace("CNY", "")?.trim()?.toDoubleOrNull() ?: 0.0
                        
                        // Rating
                        val ratingStr = element.select("span.rating_nums").text()
                        val rating = ratingStr.toFloatOrNull() ?: 0.0f
                        
                        // Description (Quote)
                        val quote = element.select("span.inq").text()

                        // Download Image
                        val localCoverPath = downloadImage(coverUrl)

                        val book = Book(
                            title = title,
                            author = author,
                            price = price,
                            coverUri = localCoverPath ?: coverUrl,
                            description = quote,
                            rating = rating,
                            status = 0
                        )
                        
                        iBookManager?.addBook(book)
                        
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "Error parsing book: ${e.message}")
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

    private fun downloadImage(url: String): String? {
        if (url.isEmpty()) return null
        try {
            val fileName = url.substringAfterLast("/")
            val file = File(getApplication<Application>().filesDir, fileName)
            
            // If file already exists, return path (simple cache)
            if (file.exists()) return file.absolutePath

            val connection = URL(url).openConnection()
            connection.connect()
            val input = connection.getInputStream()
            val output = FileOutputStream(file)
            input.use { it.copyTo(output) }
            output.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
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
