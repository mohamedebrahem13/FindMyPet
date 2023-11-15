package com.example.petme.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.petme.data.local.database.Converters
import java.util.*

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val postId: String,
    val pet_name: String,
    val pet_description: String,
    val pet_age: String,
    val pet_gender: String,
    val imageUrls: List<String>?,
    val pet_location: String,
    val userId: String,
    @TypeConverters(Converters::class)
    val timestamp: Date? = null,
)