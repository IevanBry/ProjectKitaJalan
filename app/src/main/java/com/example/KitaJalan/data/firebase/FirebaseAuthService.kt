package com.example.KitaJalan.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await()
            authResult.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}