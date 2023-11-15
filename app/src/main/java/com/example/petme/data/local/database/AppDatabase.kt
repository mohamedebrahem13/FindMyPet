package com.example.petme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.petme.data.local.dao.PostDao
import com.example.petme.data.local.dao.UserDao
import com.example.petme.data.local.model.PostEntity
import com.example.petme.data.local.model.UserEntity

@Database(entities = [UserEntity::class, PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}
