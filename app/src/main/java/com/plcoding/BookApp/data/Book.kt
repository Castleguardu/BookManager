package com.plcoding.material3expressiveguide.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "books")
@Parcelize
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val price: Double,
    val coverUri: String? = null,
    val description: String = "",
    val rating: Float = 0.0f,
    // 0 = Unread, 1 = Want to Read, 2 = Read
    val status: Int = 0 
) : Parcelable
