package com.example.findmypet.data.repository

import android.util.Log
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.E_MAIL
import com.example.findmypet.common.Constant.ID
import com.example.findmypet.common.Constant.NICKNAME
import com.example.findmypet.common.Constant.PHONE_NUMBER
import com.example.findmypet.common.Constant.POST_COUNT
import com.example.findmypet.common.Constant.PROFILE_IMAGE_PATH
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.Authenticator
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthenticator @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
    ,private val messaging: FirebaseMessaging
): Authenticator {

    override suspend fun getFirebaseUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()


    override suspend fun signUpWithEmailAndPassword(user: User, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(user.email.trim(), password.trim()).await()
        val userModel = hashMapOf(
            ID to getFirebaseUserUid(),
            E_MAIL to user.email,
            NICKNAME to user.nickname,
            PHONE_NUMBER to user.phone,
            PROFILE_IMAGE_PATH to user.imagePath,
            POST_COUNT to 0
        )

        firebaseFirestore.collection(COLLECTION_PATH).document(getFirebaseUserUid())
            .set(userModel).await()
    }


    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    override suspend fun sendPasswordResetEmail(email: String): Void =
        firebaseAuth.sendPasswordResetEmail(email).await()


    override suspend fun isCurrentUserExist() = firebaseAuth.currentUser != null

    override suspend fun getCurrentUser(): User {
        val userDocument = firebaseFirestore.collection(COLLECTION_PATH)
            .document(getFirebaseUserUid())
            .get()
            .await()

        val id = userDocument[ID] as String
        val email = userDocument[E_MAIL] as String
        val nickname = userDocument[NICKNAME] as String
        val phoneNumber = userDocument[PHONE_NUMBER] as String
        val profileImagePath = userDocument[PROFILE_IMAGE_PATH] as String

        // Retrieve POST_COUNT as an Integer, handle possible null or non-integer values
        val postCount: Int = try {
            (userDocument[POST_COUNT] as? Long)?.toInt() ?: 0
        } catch (e: Exception) {
            0 // Default to 0 if POST_COUNT is not present or not an integer
        }

        return User(id, email, nickname, phoneNumber, profileImagePath, postCount)
    }

    override suspend fun signOut() = firebaseAuth.signOut()
    override suspend fun firebaseMessagingToken(): String {
        return try {
            messaging.token.await()
        } catch (e: Exception) {
            // Handle exceptions here
            ""
        }
    }



    override suspend fun updateTokenForCurrentUser(newToken: String) {
        val userId = getFirebaseUserUid()

        // Check if a user ID is available
        if (userId.isNotEmpty()) {
            try {
                // Log the update token action
                Log.v("updateToken", "Updating token for user ID: $userId with new token: $newToken")

                // Update the Firestore document with the new token
                val userDoc = firebaseFirestore.collection(COLLECTION_PATH).document(userId)
                userDoc.update("token", newToken).await()

                // Log success
                Log.v("updateToken", "Token updated successfully")
            } catch (e: Exception) {
                // Handle exceptions here
                Log.e("updateToken", "Error updating token: ${e.message}", e)
            }
        } else {
            // Handle scenario where user ID is not available
            Log.e("updateToken", "User ID is not available, cannot update token")
        }
    }

    // New method to delete user account
    override suspend fun deleteUserAccount() {
        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                // Delete user document from FireStore
                firebaseFirestore.collection(COLLECTION_PATH).document(userId).delete().await()

                // Delete user from Firebase Authentication
                firebaseAuth.currentUser?.delete()?.await()

                Log.v("deleteUserAccount", "User account deleted successfully")
            } catch (e: Exception) {
                Log.e("deleteUserAccount", "Error deleting user account: ${e.message}", e)
            }
        } else {
            Log.e("deleteUserAccount", "User ID is not available, cannot delete account")
        }
    }

}