package com.example.petme.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petme.data.local.model.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    suspend fun getAllPosts(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts WHERE userId = :userId")
    suspend fun getPostsForUser(userId: String): List<PostEntity>


    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}

