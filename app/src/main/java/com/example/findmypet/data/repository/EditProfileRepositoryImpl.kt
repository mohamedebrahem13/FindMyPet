package com.example.findmypet.data.repository

import android.net.Uri
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.IMAGES
import com.example.findmypet.common.Constant.PROFILE_IMAGE_NAME
import com.example.findmypet.common.Constant.PROFILE_IMAGE_PATH
import com.example.findmypet.common.Resource
import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.AddImageToStorageResponse
import com.example.findmypet.domain.repository.AddImageUrlToFirestoreResponse
import com.example.findmypet.domain.repository.EditProfileRepository
import com.example.findmypet.domain.repository.GetImageUrlFromFirestoreResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : EditProfileRepository {
    override suspend fun addImageToFirebaseStorage(imageUri: Uri): AddImageToStorageResponse {
        return try {
            val downloadUrl = storage.reference.child(IMAGES).child(getFirebaseUserUid()+PROFILE_IMAGE_NAME)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun addImageUrlToFirestore(downloadUrl: Uri): AddImageUrlToFirestoreResponse {
        return try {
            db.collection(COLLECTION_PATH).document(getFirebaseUserUid()).update(PROFILE_IMAGE_PATH,downloadUrl.toString()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            error(e)
        }
    }



    override suspend fun getImageUrlFromFirestore(): GetImageUrlFromFirestoreResponse {
        return try {
            val imageUrl = db.collection(COLLECTION_PATH).document(getFirebaseUserUid()).get().await().getString(PROFILE_IMAGE_PATH)
            Resource.Success(imageUrl.toString())
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun getFirebaseUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()


    override suspend fun updateUserProfile(user: User): UpdateUserProfileResponse {


        try {
            UpdateUserProfileResponse.Loading

            val userModel = mapOf(
                Constant.NICKNAME to user.nickname,
                Constant.PHONE_NUMBER to user.phone,
                Constant.E_MAIL to user.email
            )
            val userRef = db.collection(COLLECTION_PATH).document(getFirebaseUserUid())
            userRef.update(userModel).await()

            // Update the user's profile in Firestore or any other necessary logic
            // For example, if you're using Firestore, you'd update the user document
            // with the new user data.

            // After successful update, return a success response
            return UpdateUserProfileResponse.Success
        } catch (e: Exception) {
            // Handle errors and return an error response
            return UpdateUserProfileResponse.Error(e)
        }
    }

}