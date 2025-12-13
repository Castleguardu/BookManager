package com.plcoding.material3expressiveguide.utils

import android.content.Context
import android.util.Log
import com.plcoding.material3expressiveguide.data.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object DoubanScraper {
    private const val TAG = "DoubanScraper"
    private const val DOUBAN_URL = "https://book.douban.com/top250"
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun scrapeTop250(context: Context): List<Book> = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()
        try {
            val doc = Jsoup.connect(DOUBAN_URL)
                .userAgent(USER_AGENT)
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
                    val localCoverPath = downloadImage(context, coverUrl)

                    val book = Book(
                        title = title,
                        author = author,
                        price = price,
                        coverUri = localCoverPath ?: coverUrl,
                        description = quote,
                        rating = rating,
                        status = 0
                    )
                    books.add(book)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing book item: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Douban page: ${e.message}")
            throw e
        }
        return@withContext books
    }

    private fun downloadImage(context: Context, url: String): String? {
        if (url.isEmpty()) return null
        try {
            val fileName = url.substringAfterLast("/")
            val file = File(context.filesDir, fileName)

            // Cache hit
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
}

