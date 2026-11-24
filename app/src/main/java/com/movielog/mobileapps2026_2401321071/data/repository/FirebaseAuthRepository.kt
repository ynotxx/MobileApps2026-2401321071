package com.movielog.mobileapps2026_2401321071.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.movielog.mobileapps2026_2401321071.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isUserSignedIn: Boolean
        get() = auth.currentUser != null

    suspend fun registerUser(email: String, password: String, username: String): Result<UserProfile> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")
            val profile = UserProfile(
                userId = uid,
                username = username,
                email = email,
                bio = "Movie log enthusiast"
            )
            firestore.collection("profiles").document(uid).set(profile).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Sign in failed")
            val doc = firestore.collection("profiles").document(uid).get().await()
            val profile = doc.toObject(UserProfile::class.java) ?: UserProfile(userId = uid, email = email)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getCurrentProfile(): Result<UserProfile?> {
        val uid = currentUserId ?: return Result.success(null)
        return getProfileById(uid)
    }

    suspend fun getProfileById(userId: String): Result<UserProfile?> {
        return try {
            val doc = firestore.collection("profiles").document(userId).get().await()
            val profile = doc.toObject(UserProfile::class.java)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(profile: UserProfile): Result<Unit> {
        return try {
            firestore.collection("profiles").document(profile.userId).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
