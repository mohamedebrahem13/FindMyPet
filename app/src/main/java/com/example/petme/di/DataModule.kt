package com.example.petme.di

import android.content.Context
import androidx.room.Room
import com.example.petme.data.local.dao.PostDao
import com.example.petme.data.local.dao.UserDao
import com.example.petme.data.local.database.AppDatabase
import com.example.petme.data.local.room.LocalUserDataSource
import com.example.petme.data.local.room.RoomPostDataSource
import com.example.petme.data.remote.firestore.FirebasePostDataSource
import com.example.petme.domain.datasource.LocalPostDataSource
import com.example.petme.domain.datasource.RemotePostDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalPostDataSource(postDao: PostDao): LocalPostDataSource {
        return RoomPostDataSource(postDao)
    }

    @Provides
    @Singleton
    fun provideRemotePostDataSource(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        firebaseAuth: FirebaseAuth
    ): RemotePostDataSource {
        return FirebasePostDataSource(storage,firestore , firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideLocalUserDataSource(userDao: UserDao): LocalUserDataSource {
        return LocalUserDataSource(userDao)
    }


}