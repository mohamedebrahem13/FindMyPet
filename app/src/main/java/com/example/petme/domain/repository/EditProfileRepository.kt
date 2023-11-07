package com.example.petme.domain.repository

import android.net.Uri
import com.example.petme.common.Resource
import com.example.petme.common.UpdateUserProfileResponse
import com.example.petme.data.model.User

typealias AddImageToStorageResponse = Resource<Uri>
typealias AddImageUrlToFirestoreResponse = Resource<Boolean>
typealias GetImageUrlFromFirestoreResponse = Resource<String>

interface EditProfileRepository {
    suspend fun addImageToFirebaseStorage(imageUri: Uri): AddImageToStorageResponse

    suspend fun addImageUrlToFirestore(downloadUrl: Uri): AddImageUrlToFirestoreResponse

    suspend fun getImageUrlFromFirestore(): GetImageUrlFromFirestoreResponse
    suspend fun getFirebaseUserUid(): String
    suspend fun updateUserProfile(user: User): UpdateUserProfileResponse



}