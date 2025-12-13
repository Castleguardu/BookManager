package com.plcoding.material3expressiveguide.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object BookList : Screen()
    
    @Serializable
    data class BookDetail(val bookId: Int) : Screen()
}
