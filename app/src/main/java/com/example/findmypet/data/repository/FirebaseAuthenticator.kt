package com.example.findmypet.data.repository

import android.util.Log
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.E_MAIL
import com.example.findmypet.common.Constant.ID
import com.example.findmypet.common.Constant.NICKNAME
import com.example.findmypet.common.Constant.PHONE_NUMBER
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


        val phoneExists = checkPhoneNumberExists(user.phone)

        if (phoneExists) {
            // Handle scenario where phone number already exists
            throw throw Exception("Phone number already exists")
        }
        firebaseAuth.createUserWithEmailAndPassword(user.email.trim(), password.trim()).await()
        val userModel = hashMapOf(
            ID to getFirebaseUserUid(),
            E_MAIL to user.email,
            NICKNAME to user.nickname,
            PHONE_NUMBER to user.phone,
            PROFILE_IMAGE_PATH to user.imagePath
        )

        firebaseFirestore.collection(COLLECTION_PATH).document(getFirebaseUserUid())
            .set(userModel).await()
    }

    private suspend fun checkPhoneNumberExists(phoneNumber: String): Boolean {
        val usersRef = firebaseFirestore.collection(COLLECTION_PATH)
        val snapshot = usersRef.whereEqualTo(PHONE_NUMBER, phoneNumber).get().await()
        return !snapshot.isEmpty
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    override suspend fun sendPasswordResetEmail(email: String): Void =
        firebaseAuth.sendPasswordResetEmail(email).await()


    override suspend fun isCurrentUserExist() = firebaseAuth.currentUser != null

    override suspend fun getCurrentUser(): User {
        val user =
            firebaseFirestore.collection(COLLECTION_PATH).document(getFirebaseUserUid())
                .get().await()

        return User(
            user[ID]as String,
            user[E_MAIL] as String,
            user[NICKNAME] as String,
            user[PHONE_NUMBER] as String,
            user[PROFILE_IMAGE_PATH] as String
        )    }

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
                // Example update in Fire store using the provided newToken parameter
                Log.v("updateToken","update token $newToken")
                val userDoc = firebaseFirestore.collection(COLLECTION_PATH).document(userId)
                userDoc.update("token", newToken).await()
            } catch (e: Exception) {
                // Handle exceptions here
                // Logging, error handling, etc.
            }
        } else {
            // Handle scenario where user ID is not available
        }    }



}